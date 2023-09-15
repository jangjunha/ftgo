package me.jangjunha.ftgo.consumer_service

import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.messaging.producer.jdbc.TramMessageProducerJdbcConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    TramMessageProducerJdbcConfiguration::class,
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramConsumerJdbcAutoConfiguration::class,
])
class ConsumerServiceConfiguration
