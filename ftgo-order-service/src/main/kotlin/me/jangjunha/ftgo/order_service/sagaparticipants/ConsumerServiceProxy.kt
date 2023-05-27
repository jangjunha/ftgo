package me.jangjunha.ftgo.order_service.sagaparticipants

import io.eventuate.tram.commands.common.Success
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder
import me.jangjunha.ftgo.consumer_service.api.ConsumerServiceChannels

import me.jangjunha.ftgo.consumer_service.api.command.ValidateOrderByConsumer;
import org.springframework.stereotype.Component

@Component
object ConsumerServiceProxy {
    val validateOrder = CommandEndpointBuilder
        .forCommand(ValidateOrderByConsumer::class.java)
        .withChannel(ConsumerServiceChannels.consumerServiceChannel)
        .withReply(Success::class.java)
        .build()
}
