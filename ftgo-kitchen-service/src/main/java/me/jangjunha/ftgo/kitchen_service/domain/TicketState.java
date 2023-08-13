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
        return switch (this) {
            case CREATE_PENDING -> me.jangjunha.ftgo.kitchen_service.api.TicketState.CREATE_PENDING;
            case AWAITING_ACCEPTANCE -> me.jangjunha.ftgo.kitchen_service.api.TicketState.AWAITING_ACCEPTANCE;
            case ACCEPTED -> me.jangjunha.ftgo.kitchen_service.api.TicketState.ACCEPTED;
            case PREPARING -> me.jangjunha.ftgo.kitchen_service.api.TicketState.PREPARING;
            case READY_FOR_PICKUP -> me.jangjunha.ftgo.kitchen_service.api.TicketState.READY_FOR_PICKUP;
            case PICKED_UP -> me.jangjunha.ftgo.kitchen_service.api.TicketState.PICKED_UP;
            case CANCEL_PENDING -> me.jangjunha.ftgo.kitchen_service.api.TicketState.CANCEL_PENDING;
            case CANCELLED -> me.jangjunha.ftgo.kitchen_service.api.TicketState.CANCELLED;
            case REVISION_PENDING -> me.jangjunha.ftgo.kitchen_service.api.TicketState.REVISION_PENDING;
        };
    }
}
