package me.jangjunha.ftgo.accounting_service

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("database")
data class DatabaseProperties(
    var uri: String = "",
)
