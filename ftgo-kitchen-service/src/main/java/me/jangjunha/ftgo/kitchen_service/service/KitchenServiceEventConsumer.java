package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.events.subscriber.DomainEventEnvelope;
import io.eventuate.tram.events.subscriber.DomainEventHandlers;
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder;
import me.jangjunha.ftgo.kitchen_service.domain.MenuItem;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class KitchenServiceEventConsumer {
    private KitchenService kitchenService;

    @Autowired
    public KitchenServiceEventConsumer(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    public DomainEventHandlers domainEventHandlers() {
        return DomainEventHandlersBuilder
                .forAggregateType("me.jangjunha.ftgo.restaurant_service.domain.Restaurant")
                .onEvent(RestaurantCreated.class, this::createMenu)
                .onEvent(RestaurantMenuRevised.class, this::reviseMenu)
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

    private static class RestaurantEventMapper {
        public static List<MenuItem> toMenuItems(List<me.jangjunha.ftgo.restaurant_service.api.MenuItem> menuItems) {
            return menuItems.stream()
                    .map(mi -> new MenuItem(mi.getId(), mi.getName(), mi.getPrice()))
                    .collect(Collectors.toList());
        }
    }
}
