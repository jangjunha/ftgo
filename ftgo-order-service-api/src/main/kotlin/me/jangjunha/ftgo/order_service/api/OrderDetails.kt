package me.jangjunha.ftgo.order_service.api

import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class OrderDetails(
    var lineItems: List<LineItem> = listOf(),
    var orderTotal: Money = Money.ZERO,

    var restaurantId: UUID = UUID(0, 0),
    var consumerId: UUID = UUID(0, 0),
) {
    data class LineItem(
        var quantity: Int = 0,
        var menuItemId: String = "",
        var name: String = "",
        var price: Money = Money.ZERO,
    )
}
