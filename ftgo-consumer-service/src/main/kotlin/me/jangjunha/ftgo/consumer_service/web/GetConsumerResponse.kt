package me.jangjunha.ftgo.consumer_service.web

import java.util.UUID

data class GetConsumerResponse(
    val id: UUID,
    val name: String,
)
