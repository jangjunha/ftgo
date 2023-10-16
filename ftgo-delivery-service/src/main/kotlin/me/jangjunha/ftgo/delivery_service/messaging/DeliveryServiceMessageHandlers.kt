package me.jangjunha.ftgo.delivery_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventEnvelope
import io.eventuate.tram.events.subscriber.DomainEventHandlers
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder
import me.jangjunha.ftgo.delivery_service.domain.DeliveryService
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceChannels
import me.jangjunha.ftgo.kitchen_service.api.events.TicketAcceptedEvent
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import me.jangjunha.ftgo.order_service.api.events.OrderCreated
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class DeliveryServiceMessageHandlers
@Autowired constructor(
    private val deliveryService: DeliveryService
) {

    fun domainEventHandlers(): DomainEventHandlers = DomainEventHandlersBuilder
        .forAggregateType("me.jangjunha.ftgo.restaurant_service.domain.Restaurant")
        .onEvent(RestaurantCreated::class.java, this::handleRestaurantCreated)
        .andForAggregateType(OrderServiceChannels.ORDER_EVENT_CHANNEL)
        .onEvent(OrderCreated::class.java, this::handleOrderCreated)
        .andForAggregateType(KitchenServiceChannels.TICKET_EVENT_CHANNEL)
        .onEvent(TicketAcceptedEvent::class.java, this::handleTicketAccepted)
        .build()

    private fun handleRestaurantCreated(de: DomainEventEnvelope<RestaurantCreated>) {
        val restaurantId = UUID.fromString(de.aggregateId)
        deliveryService.upsertRestaurant(restaurantId, de.event.name, de.event.address)
    }

    private fun handleOrderCreated(de: DomainEventEnvelope<OrderCreated>) {
        val orderId = UUID.fromString(de.aggregateId)
        deliveryService.createDelivery(orderId, de.event.orderDetails.restaurantId, de.event.deliveryAddress)
    }

    private fun handleTicketAccepted(de: DomainEventEnvelope<TicketAcceptedEvent>) {
        val ticketId = UUID.fromString(de.aggregateId)
        deliveryService.scheduleDelivery(ticketId, de.event.readyBy)
    }
}
