package me.jangjunha.ftgo.delivery_service

import io.eventuate.common.json.mapper.JSonMapper
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(
    EventuateTramKafkaMessageConsumerConfiguration::class,
)
class DeliveryServiceApplication

fun main(args: Array<String>) {
    JSonMapper.objectMapper.findAndRegisterModules()
    runApplication<DeliveryServiceApplication>(*args)
}
