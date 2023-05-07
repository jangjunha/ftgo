package me.jangjunha.ftgo.kitchen_service.service;

import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketDomainEvent;
import me.jangjunha.ftgo.kitchen_service.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class KitchenService {
    private final TicketDomainEventPublisher ticketDomainEventPublisher;
    private final TicketRepository ticketRepository;
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public KitchenService(
            TicketDomainEventPublisher ticketDomainEventPublisher,
            TicketRepository ticketRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.ticketDomainEventPublisher = ticketDomainEventPublisher;
        this.ticketRepository = ticketRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Transactional
    public void accept(UUID id, OffsetDateTime readyBy) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        List<TicketDomainEvent> events = ticket.accept(readyBy);
        ticketDomainEventPublisher.publish(ticket, events);
    }

    public void upsertRestaurant(UUID id, List<MenuItem> menuItems) {
        Restaurant restaurant = new Restaurant(id, menuItems);
        restaurantRepository.save(restaurant);
    }
}
