package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;
import me.jangjunha.ftgo.kitchen_service.api.CreateTicketReply;
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceChannels;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.api.commands.CancelCreateTicket;
import me.jangjunha.ftgo.kitchen_service.api.commands.ConfirmCreateTicket;
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket;
import me.jangjunha.ftgo.kitchen_service.domain.RestaurantDetailsVerificationException;
import me.jangjunha.ftgo.kitchen_service.domain.Ticket;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withFailure;
import static io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess;
import static io.eventuate.tram.sagas.participant.SagaReplyMessageBuilder.withLock;

public class KitchenServiceCommandHandler {
    private final KitchenService kitchenService;

    @Autowired
    public KitchenServiceCommandHandler(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder.fromChannel(KitchenServiceChannels.COMMAND_CHANNEL)
                .onMessage(CreateTicket.class, this::createTicket)
                .onMessage(ConfirmCreateTicket.class, this::confirmCreateTicket)
                .onMessage(CancelCreateTicket.class, this::cancelCreateTicket)
                .build();
    }

    private Message createTicket(CommandMessage<CreateTicket> cm) {
        CreateTicket command = cm.getCommand();
        UUID restaurantId = command.getRestaurantId();
        UUID orderId = command.getOrderId();
        TicketDetails ticketDetails = command.getTicketDetails();

        try {
            Ticket ticket = kitchenService.createTicket(restaurantId, orderId, ticketDetails);
            CreateTicketReply reply = new CreateTicketReply(ticket.getId(), ticket.getSequence());
            return withLock(Ticket.class, ticket.getId()).withSuccess(reply);
        } catch (RestaurantDetailsVerificationException e) {
            return withFailure();
        }
    }

    private Message confirmCreateTicket(CommandMessage<ConfirmCreateTicket> cm) {
        ConfirmCreateTicket command = cm.getCommand();
        UUID ticketId = command.getTicketId();
        kitchenService.confirmCreateTicket(ticketId);
        return withSuccess();
    }

    private Message cancelCreateTicket(CommandMessage<CancelCreateTicket> cm) {
        CancelCreateTicket command = cm.getCommand();
        UUID ticketId = command.getTicketId();
        kitchenService.cancelCreateTicket(ticketId);
        return withSuccess();
    }
}
