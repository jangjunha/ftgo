package me.jangjunha.ftgo.order_service.service

import io.eventuate.tram.events.subscriber.DomainEventDispatcher
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory
import io.eventuate.tram.sagas.participant.SagaCommandDispatcher
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.order_service.messaging.OrderEventConsumer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    TramEventSubscriberConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    SagaParticipantConfiguration::class,
    TramEventsPublisherConfiguration::class,
    SagaOrchestratorConfiguration::class,
    TramCommandsCommonAutoConfiguration::class,
])
class OrderServiceMessagingConfiguration {

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

    @Bean
    fun orderCommandHandlers(
        orderService: OrderService,
    ) = OrderCommandHandlers(orderService)

    @Bean
    fun orderEventConsumer(
        orderService: OrderService,
    ) = OrderEventConsumer(orderService)
}
