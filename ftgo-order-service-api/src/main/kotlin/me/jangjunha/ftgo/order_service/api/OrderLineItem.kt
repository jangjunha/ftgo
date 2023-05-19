package me.jangjunha.ftgo.order_service.api

import jakarta.persistence.Embeddable
import me.jangjunha.ftgo.common.Money


@Embeddable
data class OrderLineItem(
    var quantity: Int = 1,
    var menuItemId: String,
    var name: String,
    var price: Money
) {
    val total: Money
        get() = this.price.multiply(this.quantity)

    fun deltaPriceForChangedQuantity(newQuantity: Int): Money {
        return price.multiply(newQuantity - quantity)
    }
}
