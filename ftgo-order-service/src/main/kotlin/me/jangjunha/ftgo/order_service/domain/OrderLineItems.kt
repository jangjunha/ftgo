package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderLineItem

@Embeddable
data class OrderLineItems (
    @ElementCollection
    @CollectionTable(name = "order_line_items")
    var lineItems: MutableList<OrderLineItem>
) {
    val orderTotal: Money
        get() = this.lineItems.stream()
            .map { li -> li.total }
            .reduce(Money.ZERO) { lhs, rhs -> lhs.add(rhs) }
}
