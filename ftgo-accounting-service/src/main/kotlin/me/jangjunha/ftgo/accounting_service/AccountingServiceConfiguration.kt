package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import me.jangjunha.ftgo.eventuate.tram.spring.messaging.producer.kafka.TramMessageProducerKafkaConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessageProducerKafkaConfiguration::class,
])
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
