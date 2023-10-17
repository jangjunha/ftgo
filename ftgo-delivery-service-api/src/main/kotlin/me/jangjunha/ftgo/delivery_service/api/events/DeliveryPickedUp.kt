package me.jangjunha.ftgo.delivery_service.api.events

import java.time.OffsetDateTime

data class DeliveryPickedUp(
    val pickedUpAt: OffsetDateTime,
): DeliveryDomainEvent
