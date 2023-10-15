package me.jangjunha.ftgo.delivery_service.messaging

import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Configuration
@Import(
    TramEventSubscriberConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    TramNoopDuplicateMessageDetectorConfiguration::class,
)
class DeliveryServiceMessagingConfiguration
