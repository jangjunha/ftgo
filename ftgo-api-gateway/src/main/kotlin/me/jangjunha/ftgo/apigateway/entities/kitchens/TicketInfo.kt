package me.jangjunha.ftgo.apigateway.entities.kitchens

import me.jangjunha.ftgo.common.protobuf.TimestampUtils
import me.jangjunha.ftgo.kitchen_service.api.Ticket
import me.jangjunha.ftgo.kitchen_service.api.TicketState
import java.time.OffsetDateTime

data class TicketInfo(
    val state: TicketState,
    val sequence: Int?,

    val readyBy: OffsetDateTime?,
    val acceptTime: OffsetDateTime?,
    val preparingTime: OffsetDateTime?,
    val pickedUpTime: OffsetDateTime?,
    val readyForPickupTime: OffsetDateTime?,
) {
    companion object {
        fun from(ticket: Ticket) = TicketInfo(
            state = ticket.state,
            sequence = if (ticket.hasSequence()) ticket.sequence else null,
            readyBy = if (ticket.hasReadyBy()) TimestampUtils.fromTimestamp(ticket.readyBy) else null,
            acceptTime = if (ticket.hasAcceptTime()) TimestampUtils.fromTimestamp(ticket.acceptTime) else null,
            preparingTime = if (ticket.hasPreparingTime()) TimestampUtils.fromTimestamp(ticket.preparingTime) else null,
            pickedUpTime = if (ticket.hasPickedUpTime()) TimestampUtils.fromTimestamp(ticket.pickedUpTime) else null,
            readyForPickupTime = if (ticket.hasReadyForPickupTime()) TimestampUtils.fromTimestamp(ticket.readyForPickupTime) else null,
        )
    }
}
