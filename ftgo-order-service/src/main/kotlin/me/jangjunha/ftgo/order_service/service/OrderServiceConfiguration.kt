package me.jangjunha.ftgo.order_service.service

import io.eventuate.tram.events.subscriber.DomainEventDispatcher
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import me.jangjunha.ftgo.order_service.messaging.OrderEventConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    TramEventSubscriberConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramNoopDuplicateMessageDetectorConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    SagaParticipantConfiguration::class,
    TramEventsPublisherConfiguration::class,
    TramMessageProducerJdbcConfiguration::class,
])
class OrderServiceConfiguration {
    @Bean
    @Autowired
    fun domainEventDispatcher(
        orderEventConsumer: OrderEventConsumer,
        domainEventDispatcherFactory: DomainEventDispatcherFactory
    ): DomainEventDispatcher {
        return domainEventDispatcherFactory
            .make("orderServiceEvents", orderEventConsumer.domainEventHandlers());
    }

    @Bean
    @Autowired
    fun orderCommandHandlersDispatcher(
        orderCommandHandlers: OrderCommandHandlers,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory,
    ): SagaCommandDispatcher {
        return sagaCommandDispatcherFactory.make(
            "orderService",
            orderCommandHandlers.commandHandlers(),
        )
    }
}
