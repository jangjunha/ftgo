package me.jangjunha.ftgo.accounting_service.core

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class EventMetadata(
    @JsonProperty("\$correlationId")
    val correlationId: UUID? = null,

    @JsonProperty("\$causationId")
    val causationId: UUID? = null,
)
