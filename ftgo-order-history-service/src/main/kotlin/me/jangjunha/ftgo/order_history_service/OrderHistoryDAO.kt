package me.jangjunha.ftgo.order_history_service

import me.jangjunha.ftgo.order_history_service.domain.Order
import me.jangjunha.ftgo.order_history_service.domain.OrderHistory
import me.jangjunha.ftgo.order_history_service.dynamodb.SourceEvent
import me.jangjunha.ftgo.order_service.api.OrderState
import java.util.UUID

interface OrderHistoryDAO {

    fun addOrder(order: Order, eventSource: SourceEvent): Boolean

    fun updateOrderState(id: UUID, state: OrderState, eventSource: SourceEvent): Boolean

    fun findOrderById(id: UUID): Order

    fun findOrderHistory(consumerId: UUID, filter: OrderHistoryFilter): OrderHistory
}
