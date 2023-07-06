package me.jangjunha.ftgo.order_history_service.domain

import java.util.UUID

data class OrderNotFoundException(val orderId: UUID) : RuntimeException("Cannot find order $orderId")
