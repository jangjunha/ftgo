package me.jangjunha.ftgo.consumer_service

import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.consumer_service.domain.ConsumerService
import me.jangjunha.ftgo.consumer_service.domain.ConsumerServiceCommandHandlers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    TramEventsPublisherConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    SagaParticipantConfiguration::class,
    TramCommandsCommonAutoConfiguration::class,
])
class ConsumerServiceMessagingConfiguration {

    @Bean
    @Autowired
    fun commandDispatcher(
        consumerServiceCommandHandlers: ConsumerServiceCommandHandlers,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory,
    ): CommandDispatcher {
        return sagaCommandDispatcherFactory.make(
            "consumerServiceDispatcher",
            consumerServiceCommandHandlers.commandHandlers(),
        )
    }

    @Bean
    @Autowired
    fun consumerServiceCommandHandlers(
        consumerService: ConsumerService,
    ) = ConsumerServiceCommandHandlers(consumerService)
}
