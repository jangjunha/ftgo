package me.jangjunha.ftgo.apigateway.routers.kitchens

import java.time.OffsetDateTime

data class AcceptTicketRequest(
    val readyBy: OffsetDateTime,
)
