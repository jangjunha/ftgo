package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import me.jangjunha.ftgo.delivery_service.api.DeliveryServiceChannels;
import me.jangjunha.ftgo.delivery_service.api.events.DeliveryPickedUp;
import me.jangjunha.ftgo.kitchen_service.domain.MenuItem;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class KitchenServiceEventConsumer {
    private final KitchenService kitchenService;

    @Autowired
    public KitchenServiceEventConsumer(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("me.jangjunha.ftgo.restaurant_service.domain.Restaurant")
                .onEvent(RestaurantCreated.class, this::createMenu)
                .onEvent(RestaurantMenuRevised.class, this::reviseMenu)
                .andForAggregateType(DeliveryServiceChannels.DELIVERY_EVENT_CHANNEL)
                .onEvent(DeliveryPickedUp.class, this::handleDeliveryPickedUp)
                .build();
    }

    private void createMenu(DomainEventEnvelope<RestaurantCreated> de) {
        UUID restaurantId = UUID.fromString(de.getAggregateId());
        List<MenuItem> menuItems = RestaurantEventMapper.toMenuItems(de.getEvent().getMenuItems());
        kitchenService.upsertRestaurant(restaurantId, menuItems);
    }

    private void reviseMenu(DomainEventEnvelope<RestaurantMenuRevised> de) {
        UUID restaurantId = UUID.fromString(de.getAggregateId());
        List<MenuItem> menuItems = RestaurantEventMapper.toMenuItems(de.getEvent().getMenuItems());
        kitchenService.upsertRestaurant(restaurantId, menuItems);
    }

    private void handleDeliveryPickedUp(DomainEventEnvelope<DeliveryPickedUp> de) {
        UUID deliveryId = UUID.fromString(de.getAggregateId());
        kitchenService.pickUpTicket(deliveryId, de.getEvent().getPickedUpAt());
    }

    private static class RestaurantEventMapper {
        public static List<MenuItem> toMenuItems(List<me.jangjunha.ftgo.restaurant_service.api.MenuItem> menuItems) {
            return menuItems.stream()
                    .map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice()))
                    .collect(Collectors.toList());
        }
    }
}
