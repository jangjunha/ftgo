package me.jangjunha.ftgo.order_history_service.web

data class GetOrdersResponse(
    val orders: List<GetOrderResponse>,
    val startKey: String?,
)
