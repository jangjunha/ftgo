package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Embeddable
import jakarta.persistence.Transient
import me.jangjunha.ftgo.common.Money
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder
import me.jangjunha.ftgo.common.api.Money as MoneyAPI
import me.jangjunha.ftgo.order_service.api.OrderLineItem as OrderLineItemAPI
import me.jangjunha.ftgo.order_service.api.orderLineItem as orderLineItemAPI

@Embeddable
data class OrderLineItem(
    var quantity: Int = 1,
    var menuItemId: String = "",
    var name: String = "",
    var price: Money = Money.ZERO,
) {
    @get:Transient
    val total: Money
        get() = this.price.multiply(this.quantity)

    fun deltaPriceForChangedQuantity(newQuantity: Int): Money {
        return price.multiply(newQuantity - quantity)
    }

    fun export(): OrderLineItemAPI = let { li ->
        orderLineItemAPI {
            quantity = li.quantity
            menuItemId = li.menuItemId
            name = li.name
            price = li.price.toAPI()
        }
    }

    override fun equals(other: Any?): Boolean {
        return EqualsBuilder.reflectionEquals(this, other)
    }

    override fun hashCode(): Int {
        return HashCodeBuilder.reflectionHashCode(this)
    }
}
