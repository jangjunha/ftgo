package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import me.jangjunha.ftgo.accounting_service.domain.Account
import me.jangjunha.ftgo.accounting_service.api.events.AccountEvent
import me.jangjunha.ftgo.accounting_service.core.AggregateStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.*
import java.util.UUID

@Configuration
class AccountingServiceConfiguration
@Autowired constructor(val databaseProperties: DatabaseProperties) {

    @Bean
    fun dbClientSettings(): EventStoreDBClientSettings {
        return EventStoreDBConnectionString.parseOrThrow(databaseProperties.uri)
    }

    @Bean
    fun dbClient(): EventStoreDBClient {
        return EventStoreDBClient.create(dbClientSettings())
    }
}
