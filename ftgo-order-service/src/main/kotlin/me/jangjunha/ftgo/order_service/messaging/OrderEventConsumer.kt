package me.jangjunha.ftgo.order_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventEnvelope
import io.eventuate.tram.events.subscriber.DomainEventHandlers
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder
import me.jangjunha.ftgo.order_service.domain.MenuItem
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class OrderEventConsumer @Autowired constructor(private val orderService: OrderService) {
    fun domainEventHandlers(): DomainEventHandlers {
        return DomainEventHandlersBuilder
            .forAggregateType("me.jangjunha.ftgo.restaurant_service.domain.Restaurant")
            .onEvent(RestaurantCreated::class.java, this::createMenu)
            .onEvent(RestaurantMenuRevised::class.java, this::reviseMenu)
            .build();
    }

    private fun createMenu(de: DomainEventEnvelope<RestaurantCreated>) {
        val restaurantId = UUID.fromString(de.aggregateId);
        orderService.createMenu(
            restaurantId,
            de.event.name,
            de.event.menuItems.map(MenuItem::fromRestaurantMenuItem)
        )
    }

    private fun reviseMenu(de: DomainEventEnvelope<RestaurantMenuRevised>) {
        val restaurantId = UUID.fromString(de.aggregateId);
        orderService.reviseMenu(
            restaurantId,
            de.event.menuItems.map(MenuItem::fromRestaurantMenuItem)
        )
    }
}
