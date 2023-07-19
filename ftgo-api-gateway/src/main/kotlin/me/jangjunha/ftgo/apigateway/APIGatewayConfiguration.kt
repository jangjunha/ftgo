package me.jangjunha.ftgo.apigateway

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class APIGatewayConfiguration {

    @Bean
    fun webClient(): WebClient = WebClient.create()
}
