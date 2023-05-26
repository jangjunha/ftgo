package me.jangjunha.ftgo.order_service.domain

import java.util.UUID

data class RestaurantNotFoundException (
    val id: UUID
): RuntimeException(String.format("Cannot find restaurant %s", id.toString()))
