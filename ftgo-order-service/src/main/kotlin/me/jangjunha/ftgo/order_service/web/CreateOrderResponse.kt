package me.jangjunha.ftgo.order_service.web

import java.util.UUID

data class CreateOrderResponse(
    val orderId: UUID,
) {
    protected constructor(): this(UUID(0, 0))
}
