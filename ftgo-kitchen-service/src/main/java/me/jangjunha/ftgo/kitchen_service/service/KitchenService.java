package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.common.relay.Edge;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketDomainEvent;
import me.jangjunha.ftgo.kitchen_service.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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

    public List<Edge<Ticket>> listTickets(
            UUID restaurantId,
            int first,
            String after,
            int last,
            String before
    ) {
        if (restaurantId == null) {
            throw new InvalidParameterException("`restaurantId` required");
        }

        if (!after.isEmpty() && !before.isEmpty()) {
            throw new InvalidParameterException("Only one of `after` or `before` can be given.");
        }
        if (!after.isEmpty() && first == 0) {
            throw new InvalidParameterException("`first` required if `after` is given.");
        }
        if (!before.isEmpty() && last == 0) {
            throw new InvalidParameterException("`last` required if `before` is given.");
        }
        if (first == 0 && last == 0) {
            throw new InvalidParameterException("One of `first` or `last` must be given.");
        }

        Long afterSequence = !after.isEmpty() ? Long.parseLong(after) : null;
        Long beforeSequence = !before.isEmpty() ? Long.parseLong(before) : null;
        int limit = beforeSequence != null ? last : first;

        return ticketRepository
                .findAllByFilter(restaurantId, afterSequence, beforeSequence, limit)
                .stream()
                .map(ticket -> new Edge<>(ticket, ticket.getSequence().toString()))
                .toList();
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

    public void pickUpTicket(UUID id, OffsetDateTime pickedUpTime) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        ticket.setPickedUpTime(pickedUpTime);
        ticketRepository.save(ticket);
    }

    public void upsertRestaurant(UUID id, List<MenuItem> menuItems) {
        Restaurant restaurant = new Restaurant(id, menuItems);
        restaurantRepository.save(restaurant);
    }
}
