package me.jangjunha.ftgo.delivery_service.domain

import java.util.UUID

class RestaurantNotFoundException(id: UUID): RuntimeException("Cannot find restaurant $id")
