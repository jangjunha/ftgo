package me.jangjunha.ftgo.kitchen_service.api.commands;

import io.eventuate.tram.commands.CommandDestination;
import io.eventuate.tram.commands.common.Command;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;

import java.util.UUID;

@CommandDestination("restaurantService")  // TODO: 이거 뭔지 확인해야 함
public class CreateTicket implements Command {
    private UUID orderId;
    private TicketDetails ticketDetails;
    private UUID restaurantId;

    private CreateTicket() {
    }

    public CreateTicket(UUID orderId, TicketDetails ticketDetails, UUID restaurantId) {
        this.orderId = orderId;
        this.ticketDetails = ticketDetails;
        this.restaurantId = restaurantId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public TicketDetails getTicketDetails() {
        return ticketDetails;
    }

    public void setTicketDetails(TicketDetails ticketDetails) {
        this.ticketDetails = ticketDetails;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }
}
