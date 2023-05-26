package me.jangjunha.ftgo.order_service.domain

import java.util.UUID

data class OrderNotFoundException(
    val orderId: UUID
): RuntimeException(String.format("Cannot find order %s", orderId.toString()))
