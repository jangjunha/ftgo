package me.jangjunha.ftgo.apigateway.routers.kitchens

import me.jangjunha.ftgo.apigateway.entities.restaurants.RestaurantInfo
import me.jangjunha.ftgo.kitchen_service.api.TicketState
import java.time.OffsetDateTime
import java.util.UUID

data class TicketDetails(
    val id: UUID,
    val state: TicketState,
    val sequence: Int?,

    val readyBy: OffsetDateTime?,
    val acceptTime: OffsetDateTime?,
    val preparingTime: OffsetDateTime?,
    val pickedUpTime: OffsetDateTime?,
    val readyForPickupTime: OffsetDateTime?,

    val restaurant: RestaurantInfo,
)
