package me.jangjunha.ftgo.order_history_service.web

import me.jangjunha.ftgo.order_service.api.OrderState
import java.util.UUID

data class GetOrderResponse(
    val orderId: UUID,
    val status: OrderState,
    val restaurantId: UUID,
    val restaurantName: String,
)
