package me.jangjunha.ftgo.delivery_service.domain

import java.util.UUID

class DeliveryNotFoundException(id: UUID): RuntimeException("Cannot find delivery $id")
