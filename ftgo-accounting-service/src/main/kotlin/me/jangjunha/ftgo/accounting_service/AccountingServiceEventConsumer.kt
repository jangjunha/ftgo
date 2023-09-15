package me.jangjunha.ftgo.accounting_service

import io.eventuate.tram.events.subscriber.DomainEventEnvelope
import io.eventuate.tram.events.subscriber.DomainEventHandlers
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder
import me.jangjunha.ftgo.consumer_service.api.event.ConsumerCreated

class AccountingServiceEventConsumer(
    private val accountingService: AccountingService,
) {

    fun domainEventHandlers(): DomainEventHandlers {
        return DomainEventHandlersBuilder
            .forAggregateType("me.jangjunha.ftgo.consumer_service.domain.Consumer")
            .onEvent(ConsumerCreated::class.java, this::createAccount)
            .build()
    }

    fun createAccount(envelope: DomainEventEnvelope<ConsumerCreated>) {
        val event = envelope.event
        accountingService.createAccount(event.id)
    }
}
