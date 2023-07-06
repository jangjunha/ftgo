package me.jangjunha.ftgo.order_service.api

import me.jangjunha.ftgo.common.Money


data class OrderLineItem(
    var quantity: Int = 1,
    var menuItemId: String = "",
    var name: String = "",
    var price: Money = Money.ZERO,
)
