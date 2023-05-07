package me.jangjunha.ftgo.kitchen_service.api.events;

import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;

import java.util.UUID;

public class TicketCreatedEvent implements TicketDomainEvent {
    private UUID ticketId;
    private TicketDetails details;

    public TicketCreatedEvent(UUID ticketId, TicketDetails details) {
        this.ticketId = ticketId;
        this.details = details;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public TicketDetails getDetails() {
        return details;
    }

    public void setDetails(TicketDetails details) {
        this.details = details;
    }
}
