package me.jangjunha.ftgo.restaurant_service.service;

import io.eventuate.tram.events.aggregates.AbstractAggregateDomainEventPublisher;
import io.eventuate.tram.events.publisher.DomainEventPublisher;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.events.RestaurantDomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RestaurantDomainEventPublisher extends AbstractAggregateDomainEventPublisher<Restaurant, RestaurantDomainEvent> {
    @Autowired
    public RestaurantDomainEventPublisher(DomainEventPublisher domainEventPublisher) {
        super(domainEventPublisher, Restaurant.class, Restaurant::getId);
    }
}
