package me.jangjunha.ftgo.kitchen_service.api.events;

import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
