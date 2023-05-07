package me.jangjunha.ftgo.kitchen_service.api.commands;

import io.eventuate.tram.commands.common.Command;

import java.util.UUID;

public class ConfirmCreateTicket implements Command {
    private UUID ticketId;

    private ConfirmCreateTicket() {
    }

    public ConfirmCreateTicket(UUID ticketId) {
        this.ticketId = ticketId;
    }

    public UUID getTicketId() {
        return ticketId;
    }

    public void setTicketId(UUID ticketId) {
        this.ticketId = ticketId;
    }
}
