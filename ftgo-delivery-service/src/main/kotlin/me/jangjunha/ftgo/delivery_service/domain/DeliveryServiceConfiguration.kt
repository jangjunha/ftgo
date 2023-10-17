package me.jangjunha.ftgo.delivery_service.domain

import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.publisher.TramEventsPublisherConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    TramMessageProducerJdbcConfiguration::class,
    TramEventsPublisherConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
)
class DeliveryServiceConfiguration
