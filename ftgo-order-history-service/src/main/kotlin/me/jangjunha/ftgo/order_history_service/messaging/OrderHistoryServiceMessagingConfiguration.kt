package me.jangjunha.ftgo.order_history_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventDispatcher
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory
import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ConditionalOnWebApplication
@Import(value = [
    TramNoopDuplicateMessageDetectorConfiguration::class,
    TramEventSubscriberConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
])
class OrderHistoryServiceMessagingConfiguration {

    @Bean
    fun orderHistoryEventHandlers(orderHistoryDAO: OrderHistoryDAO): OrderHistoryEventHandlers {
        return OrderHistoryEventHandlers(orderHistoryDAO)
    }

    @Bean
    fun orderHistoryDomainEventDispatcher(
        orderHistoryEventHandlers: OrderHistoryEventHandlers,
        domainEventDispatcherFactory: DomainEventDispatcherFactory,
    ): DomainEventDispatcher {
        return domainEventDispatcherFactory.make(
            "orderHistoryDomainEventDispatcher",
            orderHistoryEventHandlers.domainEventHandlers(),
        )
    }
}
