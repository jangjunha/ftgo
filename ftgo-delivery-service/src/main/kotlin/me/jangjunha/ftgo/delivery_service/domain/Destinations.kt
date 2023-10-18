package me.jangjunha.ftgo.delivery_service.domain


import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "destinations")
data class Destinations(
    val kitchenServiceUrl: String
)
