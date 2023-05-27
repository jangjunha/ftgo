package me.jangjunha.ftgo.consumer_service.domain

import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess
import io.eventuate.tram.commands.consumer.CommandHandlers
import io.eventuate.tram.commands.consumer.CommandMessage
import io.eventuate.tram.messaging.common.Message
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder
import me.jangjunha.ftgo.consumer_service.api.ConsumerServiceChannels
import me.jangjunha.ftgo.consumer_service.api.command.ValidateOrderByConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class ConsumerServiceCommandHandlers @Autowired constructor(
    val consumerService: ConsumerService
) {
    fun commandHandlers(): CommandHandlers {
        return SagaCommandHandlersBuilder
            .fromChannel(ConsumerServiceChannels.consumerServiceChannel)
            .onMessage(ValidateOrderByConsumer::class.java, this::validateOrderForConsumer)
            .build()
    }

    fun validateOrderForConsumer(cm: CommandMessage<ValidateOrderByConsumer>): Message {
        return try {
            consumerService.validateOrderForConsumer(cm.command.consumerId, cm.command.orderTotal)
            withSuccess()
        } catch (e: ConsumerNotFoundException) {
            withFailure()
        }
    }
}