package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.EventStoreDBClientSettings
import com.eventstore.dbclient.EventStoreDBConnectionString
import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector
import io.eventuate.tram.events.subscriber.DomainEventDispatcher
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration
import io.eventuate.tram.spring.consumer.jdbc.TramConsumerJdbcAutoConfiguration
import io.eventuate.tram.spring.consumer.kafka.EventuateTramKafkaMessageConsumerConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.eventuate.tram.spring.messaging.producer.kafka.TramMessageProducerKafkaConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(value = [
    EventuateTramKafkaMessageConsumerConfiguration::class,
    TramMessageProducerKafkaConfiguration::class,
    TramMessagingCommonAutoConfiguration::class,
    TramEventSubscriberConfiguration::class,
    TramEventsCommonAutoConfiguration::class,
    NoopDuplicateMessageDetector::class,  // TODO:
//    TramConsumerJdbcAutoConfiguration::class,
    TramCommandsCommonAutoConfiguration::class,
    SagaParticipantConfiguration::class,
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

    @Bean
    @Autowired
    fun domainEventDispatcher(
        accountingServiceEventConsumer: AccountingServiceEventConsumer,
        domainEventDispatcherFactory: DomainEventDispatcherFactory,
    ): DomainEventDispatcher {
        return domainEventDispatcherFactory.make(
            "accountingServiceDomainEventDispatcher",
            accountingServiceEventConsumer.domainEventHandlers(),
        )
    }

    @Bean
    @Autowired
    fun commandDispatcher(
        accountingServiceCommandHandler: AccountingServiceCommandHandler,
        sagaCommandDispatcherFactory: SagaCommandDispatcherFactory,
    ): CommandDispatcher {
        return sagaCommandDispatcherFactory.make(
            "accountingServiceCommandDispatcher",
            accountingServiceCommandHandler.commandHandlers(),
        )
    }
}
