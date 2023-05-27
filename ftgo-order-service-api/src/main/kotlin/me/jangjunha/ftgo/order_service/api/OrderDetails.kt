package me.jangjunha.ftgo.order_service.api

import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class OrderDetails(
    var lineItems: List<OrderLineItem>,
    var orderTotal: Money,

    var restaurantId: UUID,
    var consumerId: UUID
) {
    protected constructor(): this(listOf(), Money.ZERO, UUID(0, 0), UUID(0, 0))
}
