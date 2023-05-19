package me.jangjunha.ftgo.order_service.domain

import me.jangjunha.ftgo.common.Money

data class LineItemQuantityChange(
    val currentOrderTotal: Money,
    val newOrderTotal: Money,
    val delta: Money,
)
