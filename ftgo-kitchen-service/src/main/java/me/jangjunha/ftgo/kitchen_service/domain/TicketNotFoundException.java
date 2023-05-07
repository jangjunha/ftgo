package me.jangjunha.ftgo.kitchen_service.domain;

import java.util.UUID;

public class TicketNotFoundException extends RuntimeException {
    public TicketNotFoundException(UUID id) {
        super(String.format("Ticket not found: %s", id.toString()));
    }
}
