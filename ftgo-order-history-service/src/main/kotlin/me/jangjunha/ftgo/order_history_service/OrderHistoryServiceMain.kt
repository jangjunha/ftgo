package me.jangjunha.ftgo.order_history_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.command.annotation.EnableCommand

@SpringBootApplication
@EnableCommand(OrderHistoryCli::class)
class OrderHistoryServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderHistoryServiceApplication>(*args)
}
