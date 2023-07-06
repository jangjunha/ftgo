package me.jangjunha.ftgo.order_history_service.dynamodb

import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import me.jangjunha.ftgo.order_history_service.domain.Order
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException


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

    private fun ignoreConditionalCheck(eventSource: SourceEvent, callable: () -> Unit): Boolean {
        return try {
            callable()
            true
        } catch (e: ConditionalCheckFailedException) {
            logger.info("not updated {}", eventSource)
            false
        }
    }
}
