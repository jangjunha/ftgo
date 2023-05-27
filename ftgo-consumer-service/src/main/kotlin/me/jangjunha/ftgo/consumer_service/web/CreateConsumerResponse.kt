package me.jangjunha.ftgo.consumer_service.web

import java.util.UUID

data class CreateConsumerResponse(
    val id: UUID,
    val name: String,
)
