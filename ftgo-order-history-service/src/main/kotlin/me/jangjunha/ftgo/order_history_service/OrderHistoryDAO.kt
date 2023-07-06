package me.jangjunha.ftgo.order_history_service

import me.jangjunha.ftgo.order_history_service.domain.Order
import me.jangjunha.ftgo.order_history_service.dynamodb.SourceEvent

interface OrderHistoryDAO {

    fun addOrder(order: Order, eventSource: SourceEvent): Boolean
}
