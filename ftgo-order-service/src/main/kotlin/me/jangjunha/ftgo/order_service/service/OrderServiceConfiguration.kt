package me.jangjunha.ftgo.order_service.service

import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    value = [
        EventuateTramKafkaMessageConsumerConfiguration::class,
        TramMessageProducerJdbcConfiguration::class,
        TramNoopDuplicateMessageDetectorConfiguration::class,
    ]
)
class OrderServiceConfiguration
