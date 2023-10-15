package me.jangjunha.ftgo.delivery_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory
import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.delivery_service.domain.DeliveryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Configuration
@Import(
    TramEventSubscriberConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    TramNoopDuplicateMessageDetectorConfiguration::class,
)
class DeliveryServiceMessagingConfiguration {

    @Bean
    fun deliveryMessageHandlers(deliveryService: DeliveryService) = DeliveryServiceMessageHandlers(deliveryService)

    @Bean
    fun domainEventDispatcher(
        deliveryMessageHandlers: DeliveryServiceMessageHandlers,
        domainEventDispatcherFactory: DomainEventDispatcherFactory,
    ) = domainEventDispatcherFactory.make("deliveryServiceEvents", deliveryMessageHandlers.domainEventHandlers())
}
