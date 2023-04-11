package me.jangjunha.ftgo.kitchen_service.domain;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(long ticketId) {
        super(String.format("Ticket not found: %d", ticketId));
    }
}
