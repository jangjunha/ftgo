package me.jangjunha.ftgo.delivery_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventEnvelope
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder
import me.jangjunha.ftgo.delivery_service.domain.DeliveryService
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class DeliveryServiceMessageHandlers
@Autowired constructor(
    private val deliveryService: DeliveryService
) {

    fun domainEventHandlers() = DomainEventHandlersBuilder
        .forAggregateType("me.jangjunha.ftgo.restaurant_service.domain.Restaurant")
        .onEvent(RestaurantCreated::class.java, this::handleRestaurantCreated)
        .build()

    private fun handleRestaurantCreated(de: DomainEventEnvelope<RestaurantCreated>) {
        val restaurantId = UUID.fromString(de.aggregateId)
        deliveryService.upsertRestaurant(restaurantId, de.event.name, de.event.address)
    }
}
