package me.jangjunha.ftgo.kitchen_service.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import jakarta.persistence.*;
import me.jangjunha.ftgo.common.UnsupportedStateTransitionException;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketAcceptedEvent;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketCreatedEvent;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketDomainEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

@Entity
@Table(
        name = "tickets",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"restaurantId", "sequence"})
        }
)
@Access(AccessType.FIELD)
public class Ticket {
    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private TicketState state;
    @Enumerated(EnumType.STRING)
    private TicketState previousState;

    @Column(nullable = false)
    private UUID restaurantId;
    private Long sequence;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ticket_line_items")
    private List<TicketLineItem> lineItems;

    private OffsetDateTime readyBy;
    private OffsetDateTime acceptTime;
    private OffsetDateTime preparingTime;
    private OffsetDateTime pickedUpTime;
    private OffsetDateTime readyForPickupTime;

    public Ticket() {
    }

    public Ticket(UUID id, UUID restaurantId, Long sequence, me.jangjunha.ftgo.kitchen_service.api.TicketDetails details) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.sequence = sequence;
        this.state = TicketState.CREATE_PENDING;
        this.lineItems = TicketLineItem.fromTicketDetails(details).collect(Collectors.toList());
    }

    public static ResultWithDomainEvents<Ticket, TicketDomainEvent> create(
            UUID id,
            UUID restaurantId,
            Long sequence,
            me.jangjunha.ftgo.kitchen_service.api.TicketDetails details
    ) {
        return new ResultWithDomainEvents<>(new Ticket(id, restaurantId, sequence, details));
    }

    public List<TicketDomainEvent> confirmCreate() {
        switch (state) {
            case CREATE_PENDING:
                this.state = TicketState.AWAITING_ACCEPTANCE;
                return singletonList(new TicketCreatedEvent(id, TicketLineItem.toTicketDetails(getLineItems().stream())));
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<TicketDomainEvent> cancelCreate() {
        switch (state) {
            case CREATE_PENDING:
                this.state = TicketState.CANCELLED;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<TicketDomainEvent> accept(OffsetDateTime readyBy) {
        switch (state) {
            case AWAITING_ACCEPTANCE -> {
                this.state = TicketState.ACCEPTED;
                this.acceptTime = OffsetDateTime.now();
                if (!acceptTime.isBefore(readyBy))
                    throw new IllegalArgumentException(String.format("readyBy %s is not after now %s", readyBy, acceptTime));
                this.readyBy = readyBy;
                return singletonList(new TicketAcceptedEvent(readyBy));
            }
            case ACCEPTED -> throw new AlreadyAcceptedException();
            default -> throw new UnsupportedStateTransitionException(state);
        }
    }

    public List<TicketDomainEvent> cancel() {
        switch (state) {
            case AWAITING_ACCEPTANCE:
            case ACCEPTED:
                this.previousState = state;
                this.state = TicketState.CANCEL_PENDING;
                return emptyList();
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TicketState getState() {
        return state;
    }

    public void setState(TicketState state) {
        this.state = state;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public List<TicketLineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<TicketLineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public OffsetDateTime getReadyBy() {
        return readyBy;
    }

    public void setReadyBy(OffsetDateTime readyBy) {
        this.readyBy = readyBy;
    }

    public OffsetDateTime getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(OffsetDateTime acceptTime) {
        this.acceptTime = acceptTime;
    }

    public OffsetDateTime getPreparingTime() {
        return preparingTime;
    }

    public void setPreparingTime(OffsetDateTime preparingTime) {
        this.preparingTime = preparingTime;
    }

    public OffsetDateTime getPickedUpTime() {
        return pickedUpTime;
    }

    public void setPickedUpTime(OffsetDateTime pickedUpTime) {
        this.pickedUpTime = pickedUpTime;
    }

    public OffsetDateTime getReadyForPickupTime() {
        return readyForPickupTime;
    }

    public void setReadyForPickupTime(OffsetDateTime readyForPickupTime) {
        this.readyForPickupTime = readyForPickupTime;
    }
}
