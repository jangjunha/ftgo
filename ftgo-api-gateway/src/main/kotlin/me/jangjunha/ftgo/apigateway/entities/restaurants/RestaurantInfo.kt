package me.jangjunha.ftgo.apigateway.entities.restaurants

import me.jangjunha.ftgo.restaurant_service.api.Restaurant
import java.util.UUID

data class RestaurantInfo(
    val id: UUID,
    val name: String,
) {
    companion object {
        fun from(restaurant: Restaurant) = RestaurantInfo(
            restaurant.id,
            restaurant.name,
        )
    }
}
