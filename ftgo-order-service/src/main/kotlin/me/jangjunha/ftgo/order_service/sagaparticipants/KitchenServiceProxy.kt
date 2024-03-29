package me.jangjunha.ftgo.order_service.sagaparticipants

import io.eventuate.tram.commands.common.Success
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceChannels
import me.jangjunha.ftgo.kitchen_service.api.commands.CancelCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.ConfirmCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket
import org.springframework.stereotype.Component

@Component
object KitchenServiceProxy {
    val create: CommandEndpoint<CreateTicket> = CommandEndpointBuilder
        .forCommand(CreateTicket::class.java)
        .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
        .withReply(Success::class.java)
        .build()

    val confirm: CommandEndpoint<ConfirmCreateTicket> = CommandEndpointBuilder
        .forCommand(ConfirmCreateTicket::class.java)
        .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
        .withReply(Success::class.java)
        .build()

    val cancel: CommandEndpoint<CancelCreateTicket> = CommandEndpointBuilder
        .forCommand(CancelCreateTicket::class.java)
        .withChannel(KitchenServiceChannels.COMMAND_CHANNEL)
        .withReply(Success::class.java)
        .build()
}
