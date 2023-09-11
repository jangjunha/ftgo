package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.CollectionTable
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderRevision
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

@Embeddable
data class OrderLineItems (
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_line_items")
    var lineItems: MutableList<OrderLineItem>
) {
    val orderTotal: Money
        get() = this.lineItems.stream()
            .map { li -> li.total }
            .reduce(Money.ZERO) { lhs, rhs -> lhs.add(rhs) }

    fun lineItemQuantityChange(orderRevision: OrderRevision): LineItemQuantityChange {
        val delta = orderRevision.revisedOrderLineItems
            .map { revisedLineItem ->
                val lineItem = lineItems.find {
                    it.menuItemId == revisedLineItem.menuItemId
                } ?: error("Cannot find lineItem from existing order")
                lineItem.deltaPriceForChangedQuantity(revisedLineItem.quantity)
            }
            .fold(Money.ZERO, Money::add)
        val newOrderTotal = orderTotal.add(delta)
        return LineItemQuantityChange(orderTotal, newOrderTotal, delta)
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }
}
