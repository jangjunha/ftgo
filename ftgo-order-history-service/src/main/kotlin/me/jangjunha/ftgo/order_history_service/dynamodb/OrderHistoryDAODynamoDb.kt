package me.jangjunha.ftgo.order_history_service.dynamodb

import com.fasterxml.jackson.databind.ObjectMapper
import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import me.jangjunha.ftgo.order_history_service.OrderHistoryFilter
import me.jangjunha.ftgo.order_history_service.domain.Order
import me.jangjunha.ftgo.order_history_service.domain.OrderHistory
import me.jangjunha.ftgo.order_history_service.domain.OrderNotFoundException
import me.jangjunha.ftgo.order_service.api.OrderState
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException
import java.util.*


class OrderHistoryDAODynamoDb(
    private val orderTable: DynamoDbTable<Order>,
) : OrderHistoryDAO {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun addOrder(order: Order, eventSource: SourceEvent): Boolean {
        return ignoreConditionalCheck(eventSource) {
            orderTable.putItem { request ->
                request
                    .item(order)
                    .conditionExpression(eventSource.ignoreIfDuplicateExpression)
            }
        }
    }

    override fun updateOrderState(id: UUID, state: OrderState, eventSource: SourceEvent): Boolean {
        val key = Key.builder().partitionValue(id.toString()).build()
        val order = orderTable.getItem(key) ?: throw OrderNotFoundException(id)
        order.status = state
        return ignoreConditionalCheck(eventSource) {
            orderTable.updateItem { request ->
                request
                    .item(order)
                    .conditionExpression(eventSource.ignoreIfDuplicateExpression)
            }
        }
    }

    override fun findOrderHistory(consumerId: UUID, filter: OrderHistoryFilter): OrderHistory {
        val index = orderTable.index(Order.consumerCreationDateIndexName)
        val pagedResult = index.query { q ->
            q.scanIndexForward(false)

            q.queryConditional(QueryConditional.sortGreaterThanOrEqualTo { builder ->
                builder
                    .partitionValue(consumerId.toString())
                    .sortValue(ComparableOffsetDateTimeAttributeConverter().transformFrom(filter.since))
            })

            val keywordsExpr = filter.keywords
                .mapIndexed { idx, keyword ->
                    val placeholder = ":keyword$idx"
                    Expression.builder()
                        .expression("contains(keywords, $placeholder")
                        .putExpressionValue(placeholder, AttributeValue.fromS(keyword))
                        .build()
                }.fold(null) { acc: Expression?, exp ->
                    Expression.join(acc, exp, " OR ")
                }
            val statusExpr = if (filter.status != null) {
                Expression.builder()
                    .expression("#status = :status")
                    .putExpressionName("#status", "status")
                    .putExpressionValue(":status", AttributeValue.fromS(filter.status.name))
                    .build()
            } else {
                null
            }
            q.filterExpression(Expression.join(
                keywordsExpr,
                statusExpr,
                " AND "
            ))

            if (filter.startKeyToken != null) {
                q.exclusiveStartKey(StartKeyToken.deserialize(filter.startKeyToken))
            }

            if (filter.pageSize != null) {
                q.limit(filter.pageSize)
            }
        }

        val page = pagedResult.iterator().next()
        return OrderHistory(
            page.items(),
            page.lastEvaluatedKey()?.let(StartKeyToken::serialize),
        )
    }

    private fun ignoreConditionalCheck(eventSource: SourceEvent, callable: () -> Unit): Boolean {
        return try {
            callable()
            true
        } catch (e: ConditionalCheckFailedException) {
            logger.info("not updated {}", eventSource)
            false
        }
    }

    object StartKeyToken {
        private val mapper = ObjectMapper()

        fun serialize(lastEvaluatedKey: Map<String, AttributeValue>): String {
            val mapped = lastEvaluatedKey.mapValues { (_, value) ->
                value.s() ?: value.n().toLong()
            }
            return mapper.writeValueAsString(mapped)
        }

        fun deserialize(token: String): Map<String, AttributeValue> {
            val mapped = mapper.readValue(token, Map::class.java) as Map<String, *>
            return mapped.mapValues { (key, value) ->
                when (value) {
                    is String -> AttributeValue.fromS(value)
                    is Long -> AttributeValue.fromN(value.toString())
                    is Int -> AttributeValue.fromN(value.toString())
                    else -> throw RuntimeException("Cannot deserialize startToken entry $key = $value")
                }
            }
        }
    }
}
