package me.jangjunha.ftgo.consumer_service

import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration
import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import me.jangjunha.ftgo.consumer_service.domain.ConsumerServiceCommandHandlers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    TramEventsPublisherConfiguration::class,
    TramMessageProducerJdbcConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    SagaParticipantConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramConsumerJdbcAutoConfiguration::class,
    TramCommandsCommonAutoConfiguration::class,
])
class ConsumerServiceConfiguration {
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
}