package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.*

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
