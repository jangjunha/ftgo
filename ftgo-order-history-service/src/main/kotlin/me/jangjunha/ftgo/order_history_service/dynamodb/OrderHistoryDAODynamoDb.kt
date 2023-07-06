package me.jangjunha.ftgo.order_history_service.dynamodb

import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import me.jangjunha.ftgo.order_history_service.domain.Order
import me.jangjunha.ftgo.order_history_service.domain.OrderNotFoundException
import me.jangjunha.ftgo.order_service.api.OrderState
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
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
