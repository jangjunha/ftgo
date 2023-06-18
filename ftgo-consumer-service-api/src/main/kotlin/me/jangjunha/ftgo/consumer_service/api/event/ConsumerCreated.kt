package me.jangjunha.ftgo.consumer_service.api.event

import io.eventuate.tram.events.common.DomainEvent
import java.util.UUID

data class ConsumerCreated(
    val id: UUID = UUID(0, 0),
): DomainEvent
