package me.jangjunha.ftgo.apigateway

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "destinations")
data class Destinations(
    val kitchenServiceUrl: String,
    val restaurantServiceUrl: String,
)
