package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Embeddable
import jakarta.persistence.Transient
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.common.api.Money as MoneyAPI
import me.jangjunha.ftgo.order_service.api.OrderLineItem as OrderLineItemAPI

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

    fun export(): OrderLineItemAPI {
        return OrderLineItemAPI.newBuilder()
            .setQuantity(quantity)
            .setMenuItemId(menuItemId)
            .setName(name)
            .setPrice(MoneyAPI.newBuilder().setAmount(price.amount.toString()).build())
            .build()
    }
}
