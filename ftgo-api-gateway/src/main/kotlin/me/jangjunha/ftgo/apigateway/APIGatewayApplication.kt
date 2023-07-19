package me.jangjunha.ftgo.apigateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class APIGatewayApplication

fun main(args: Array<String>) {
    runApplication<APIGatewayApplication>(*args)
}
