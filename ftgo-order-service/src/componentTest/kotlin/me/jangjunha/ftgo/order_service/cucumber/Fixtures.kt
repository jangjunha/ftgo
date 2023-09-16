package me.jangjunha.ftgo.order_service.cucumber

import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.domain.MenuItem
import me.jangjunha.ftgo.order_service.domain.Restaurant
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated
import java.util.*
import me.jangjunha.ftgo.restaurant_service.api.MenuItem as MenuItemAPI

object Fixtures {

    val A_CAFE = Restaurant(
        UUID.fromString("7136c3ee-f754-4e76-999c-b951b87f87fe"),
        mutableListOf(
            MenuItem("americano", "Americano", Money("2500")),
        ),
        "A Cafe",
    )

    val CONSUMER_ID = UUID.fromString("17ac2832-5089-4e50-954f-8c78b0ace81f")

    fun makeACafeCreatedEvent() =
        RestaurantCreated(A_CAFE.name, A_CAFE.menuItems.map { MenuItemAPI(it.id, it.name, it.price) })
}
