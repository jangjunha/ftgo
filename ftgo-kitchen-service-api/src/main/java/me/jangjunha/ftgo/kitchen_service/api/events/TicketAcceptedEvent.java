package me.jangjunha.ftgo.kitchen_service.api.events;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.time.OffsetDateTime;

public class TicketAcceptedEvent implements TicketDomainEvent {
    private OffsetDateTime readyBy;

    public TicketAcceptedEvent(OffsetDateTime readyBy) {
        this.readyBy = readyBy;
    }

    public OffsetDateTime getReadyBy() {
        return readyBy;
    }

    public void setReadyBy(OffsetDateTime readyBy) {
        this.readyBy = readyBy;
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
