package me.jangjunha.ftgo.delivery_service.domain

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher
import io.eventuate.tram.events.publisher.DomainEventPublisher
import me.jangjunha.ftgo.delivery_service.api.events.DeliveryDomainEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DeliveryDomainEventPublisher
@Autowired constructor(
    eventPublisher: DomainEventPublisher,
) : AbstractAggregateDomainEventPublisher<Delivery, DeliveryDomainEvent>(
    eventPublisher,
    Delivery::class.java,
    Delivery::id
)
