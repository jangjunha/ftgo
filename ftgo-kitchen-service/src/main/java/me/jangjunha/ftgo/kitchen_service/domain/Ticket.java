package me.jangjunha.ftgo.kitchen_service.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


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

    public Long getId() {
        return id;
    }
}
