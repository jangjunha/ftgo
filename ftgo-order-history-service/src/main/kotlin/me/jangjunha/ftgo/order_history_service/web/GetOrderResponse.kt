package me.jangjunha.ftgo.order_history_service.web

import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
import java.time.OffsetDateTime
import java.util.UUID

data class GetOrderResponse(
    val orderId: UUID,
    val status: OrderState,
    val restaurantId: UUID,
    val restaurantName: String,
    val consumerId: UUID,
    val creationDate: OffsetDateTime,
    val lineItems: List<LineItem>,
) {
    data class LineItem(
        val quantity: Int,
        val menuItemId: String,
        val name: String,
        val price: Money,
    )
}
