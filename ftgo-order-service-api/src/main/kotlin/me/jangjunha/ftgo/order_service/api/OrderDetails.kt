package me.jangjunha.ftgo.order_service.api

import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class OrderDetails(
    var lineItems: List<LineItem>,
    var orderTotal: Money,

    var restaurantId: UUID,
    var consumerId: UUID,
) {
    data class LineItem(
        var quantity: Int = 0,
        var menuItemId: String = "",
        var name: String = "",
        var price: Money = Money.ZERO,
    )

    internal constructor() : this(emptyList(), Money.ZERO, UUID(0, 0), UUID(0, 0))
}
