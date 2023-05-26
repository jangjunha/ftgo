package me.jangjunha.ftgo.order_service.sagaparticipants

import io.eventuate.tram.commands.common.Success
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import org.springframework.stereotype.Component

@Component
object OrderServiceProxy {
    val reject = CommandEndpointBuilder
        .forCommand(RejectOrderCommand::class.java)
        .withChannel(OrderServiceChannels.COMMAND_CHANNEL)
        .withReply(Success::class.java)
        .build()

    val approve = CommandEndpointBuilder
        .forCommand(ApproveOrderCommand::class.java)
        .withChannel(OrderServiceChannels.COMMAND_CHANNEL)
        .withReply(Success::class.java)
        .build()
}
