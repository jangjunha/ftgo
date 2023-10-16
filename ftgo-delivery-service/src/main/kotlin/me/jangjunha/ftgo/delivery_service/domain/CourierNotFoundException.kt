package me.jangjunha.ftgo.delivery_service.domain

import java.util.UUID

class CourierNotFoundException(id: UUID): RuntimeException("Cannot find courier $id")
