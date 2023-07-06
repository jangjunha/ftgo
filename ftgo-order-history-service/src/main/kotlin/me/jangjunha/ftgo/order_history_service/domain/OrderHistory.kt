package me.jangjunha.ftgo.order_history_service.domain

data class OrderHistory(
    val orders: List<Order>,
    val startKey: String?,
)
