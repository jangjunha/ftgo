package me.jangjunha.ftgo.kitchen_service.domain;

public enum TicketState {
    CREATE_PENDING,
    AWAITING_ACCEPTANCE,
    ACCEPTED,
    PREPARING,
    READY_FOR_PICKUP,
    PICKED_UP,
    CANCEL_PENDING,
    CANCELLED,
    REVISION_PENDING;

    public me.jangjunha.ftgo.kitchen_service.api.TicketState toAPI() {
        switch (this) {
            case CREATE_PENDING -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.CREATE_PENDING;
            }
            case AWAITING_ACCEPTANCE -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.AWAITING_ACCEPTANCE;
            }
            case ACCEPTED -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.ACCEPTED;
            }
            case PREPARING -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.PREPARING;
            }
            case READY_FOR_PICKUP -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.READY_FOR_PICKUP;
            }
            case PICKED_UP -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.PICKED_UP;
            }
            case CANCEL_PENDING -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.CANCEL_PENDING;
            }
            case CANCELLED -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.CANCELLED;
            }
            case REVISION_PENDING -> {
                return me.jangjunha.ftgo.kitchen_service.api.TicketState.REVISION_PENDING;
            }
        }
        throw new RuntimeException("Cannot serialize %s".formatted(this));
    }
}
