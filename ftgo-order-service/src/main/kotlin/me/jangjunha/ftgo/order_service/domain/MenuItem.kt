package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money

@Embeddable
@Access(AccessType.FIELD)
data class MenuItem(
    var id: String,
    var name: String,
    var price: Money,
) {
    companion object {
        fun fromRestaurantMenuItem(mi: me.jangjunha.ftgo.restaurant_service.api.MenuItem): MenuItem {
            return MenuItem(mi.id, mi.name, mi.price)
        }
    }
}
