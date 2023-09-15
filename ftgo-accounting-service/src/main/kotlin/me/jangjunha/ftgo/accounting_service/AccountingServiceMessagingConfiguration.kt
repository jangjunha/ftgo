package me.jangjunha.ftgo.accounting_service

import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector
import io.eventuate.tram.events.subscriber.DomainEventDispatcher
import io.eventuate.tram.events.subscriber.DomainEventDispatcherFactory
import io.eventuate.tram.sagas.participant.SagaCommandDispatcherFactory
import io.eventuate.tram.sagas.spring.participant.SagaParticipantConfiguration
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.events.subscriber.TramEventSubscriberConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    value = [
        TramMessagingCommonAutoConfiguration::class,
        TramEventSubscriberConfiguration::class,
        TramEventsCommonAutoConfiguration::class,
        NoopDuplicateMessageDetector::class,
        TramCommandsCommonAutoConfiguration::class,
        SagaParticipantConfiguration::class,
    ]
)
class AccountingServiceMessagingConfiguration {

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

    @Bean
    @Autowired
    fun accountingServiceEventConsumer(accountingService: AccountingService) =
        AccountingServiceEventConsumer(accountingService)

    @Bean
    @Autowired
    fun accountingServiceCommandHandler(accountingService: AccountingService) =
        AccountingServiceCommandHandler(accountingService)
}
