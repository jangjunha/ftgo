package me.jangjunha.ftgo.accounting_service.core

import java.util.UUID

data class EventMetadata(
    val streamId: String = "",
    val eventId: UUID = UUID(0, 0),
    val streamPosition: Long = 0,
    val logPosition: Long = 0,
    val eventType: String = "",
)
