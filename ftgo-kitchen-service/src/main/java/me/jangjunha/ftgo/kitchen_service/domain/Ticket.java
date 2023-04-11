package me.jangjunha.ftgo.kitchen_service.domain;

import jakarta.persistence.*;
import me.jangjunha.ftgo.common.UnsupportedStateTransitionException;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketAcceptedEvent;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketDomainEvent;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;


@Entity
@Table(name = "tickets")
@Access(AccessType.FIELD)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TicketState state;
    @Enumerated(EnumType.STRING)
    private TicketState previousState;

    private UUID restaurantId;

    @ElementCollection
    @CollectionTable(name = "ticket_line_items")
    private List<TicketLineItem> lineItems;

    private OffsetDateTime readyBy;
    private OffsetDateTime acceptTime;
    private OffsetDateTime preparingTime;
    private OffsetDateTime pickedUpTime;
    private OffsetDateTime readyForPickupTime;

    public Ticket() {
    }

    public Ticket(Long id, UUID restaurantId, me.jangjunha.ftgo.kitchen_service.api.TicketDetails details) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.state = TicketState.CREATE_PENDING;
        this.lineItems = details.getLineItems().stream()
                .map(li -> new TicketLineItem(
                        li.getQuantity(),
                        li.getMenuItemId(),
                        li.getName()
                ))
                .collect(Collectors.toList());
    }

    public List<TicketDomainEvent> accept(OffsetDateTime readyBy) {
        switch (state) {
            case AWAITING_ACCEPTANCE:
                this.acceptTime = OffsetDateTime.now();
                if (!acceptTime.isBefore(readyBy))
                    throw new IllegalArgumentException(String.format("readyBy %s is not after now %s", readyBy, acceptTime));
                this.readyBy = readyBy;
                return singletonList(new TicketAcceptedEvent(readyBy));
            default:
                throw new UnsupportedStateTransitionException(state);
        }
    }

    public Long getId() {
        return id;
    }
}
