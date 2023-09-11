package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
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

    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
    }

    @Transactional
    public Ticket createTicket(UUID restaurantId, UUID orderId, TicketDetails ticketDetails) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RestaurantDetailsVerificationException();
        }
        long sequence = ticketRepository.getLastSequence(restaurantId) + 1;

        ResultWithDomainEvents<Ticket, TicketDomainEvent> twe = Ticket.create(orderId, restaurantId, sequence, ticketDetails);
        ticketRepository.save(twe.result);
        ticketDomainEventPublisher.publish(twe.result, twe.events);
        return twe.result;
    }

    @Transactional
    public void confirmCreateTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        List<TicketDomainEvent> events = ticket.confirmCreate();
        ticketDomainEventPublisher.publish(ticket, events);
    }

    @Transactional
    public void cancelTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        List<TicketDomainEvent> events = ticket.cancel();
        ticketDomainEventPublisher.publish(ticket, events);
    }

    @Transactional
    public void cancelCreateTicket(UUID id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        List<TicketDomainEvent> events = ticket.cancelCreate();
        ticketDomainEventPublisher.publish(ticket, events);
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
