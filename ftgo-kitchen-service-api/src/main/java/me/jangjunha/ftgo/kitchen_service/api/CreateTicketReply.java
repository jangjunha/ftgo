package me.jangjunha.ftgo.kitchen_service.api;

import java.util.UUID;

public class CreateTicketReply {
    private UUID ticketId;
    private long sequence;

    public CreateTicketReply(UUID ticketId, long sequence) {
        this.ticketId = ticketId;
        this.sequence = sequence;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }
}
