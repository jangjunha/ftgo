package me.jangjunha.ftgo.order_service.api.events

import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderRevision

data class OrderRevisionProposed(
    val orderRevision: OrderRevision,
    val currentOrderTotal: Money,
    val newOrderTotal: Money,
): OrderDomainEvent
