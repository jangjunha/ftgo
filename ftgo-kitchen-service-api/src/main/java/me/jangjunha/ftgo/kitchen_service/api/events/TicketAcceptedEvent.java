package me.jangjunha.ftgo.kitchen_service.api.events;

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
}
