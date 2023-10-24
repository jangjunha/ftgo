package me.jangjunha.ftgo.order_history_service

import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@Import(
    EventuateTramKafkaMessageConsumerConfiguration::class,
)
@EnableCommand(OrderHistoryCli::class)
class OrderHistoryServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderHistoryServiceApplication>(*args)
}
