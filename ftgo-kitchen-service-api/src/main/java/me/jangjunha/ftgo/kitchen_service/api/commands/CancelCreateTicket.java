package me.jangjunha.ftgo.kitchen_service.api.commands;

import io.eventuate.tram.commands.common.Command;

import java.util.UUID;

public class CancelCreateTicket implements Command {
    private UUID ticketId;

    private CancelCreateTicket() {
    }

    public CancelCreateTicket(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }
}
