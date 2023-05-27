package me.jangjunha.ftgo.order_service.domain

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher
import io.eventuate.tram.events.publisher.DomainEventPublisher
import me.jangjunha.ftgo.order_service.api.events.OrderDomainEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderDomainEventPublisher @Autowired constructor(
    eventPublisher: DomainEventPublisher,
): AbstractAggregateDomainEventPublisher<Order, OrderDomainEvent>(
    eventPublisher,
    Order::class.java,
    Order::id,
)
