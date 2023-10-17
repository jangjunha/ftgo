package me.jangjunha.ftgo.delivery_service.api.events

import java.time.OffsetDateTime

data class DeliveryDropoff(
    val dropoffAt: OffsetDateTime,
): DeliveryDomainEvent
