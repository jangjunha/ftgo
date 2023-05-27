package me.jangjunha.ftgo.order_service.web

import me.jangjunha.ftgo.order_service.domain.MenuItemIdAndQuantity
import java.util.UUID

data class CreateOrderRequest(
    val restaurantId: UUID,
    val consumerId: UUID,
    val items: List<MenuItemIdAndQuantity>,
    val deliveryAddress: String,
) {
    protected constructor(): this(UUID(0, 0), UUID(0, 0), listOf(), "")
}
