package me.jangjunha.ftgo.kitchen_service.service;

import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.api.commands.CancelCreateTicket;
import me.jangjunha.ftgo.kitchen_service.api.commands.ConfirmCreateTicket;
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket;
import me.jangjunha.ftgo.kitchen_service.domain.TicketState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static me.jangjunha.ftgo.kitchen_service.KitchenFixtures.*;
import static io.eventuate.tram.testing.commands.CommandMessageHandlerUnitTestSupport.given;
import static org.mockito.Mockito.*;

class KitchenServiceCommandHandlerTest {

    private static UUID ORDER_ID = UUID.fromString("1c60229c-c45e-40a8-8fe7-51fe1d2d41cc");

    private KitchenService kitchenService;
    private KitchenServiceCommandHandler commandHandler;

    @BeforeEach
    void setUp() {
        kitchenService = mock(KitchenService.class);
        commandHandler = new KitchenServiceCommandHandler(kitchenService);
    }

    @Test
    void createTicket() {
        when(kitchenService.createTicket(any(), any(), any())).thenReturn(makeTicket(ORDER_ID, TicketState.CREATE_PENDING));

        given()
            .commandHandlers(commandHandler.commandHandlers())
        .when()
            .receives(new CreateTicket(ORDER_ID, new TicketDetails(List.of(
                    new TicketDetails.LineItem(1, "latte", "Cafe Latte")
            )), A_CAFE_ID))
        .then()
            .verify((m) -> verify(kitchenService).createTicket(A_CAFE_ID, ORDER_ID, new TicketDetails(List.of(
                    new TicketDetails.LineItem(1, "latte", "Cafe Latte")
            ))));
    }

    @Test
    void confirmCreateTicket() {
        given()
            .commandHandlers(commandHandler.commandHandlers())
        .when()
            .receives(new ConfirmCreateTicket(ORDER_ID))
        .then()
            .verify((m) -> verify(kitchenService).confirmCreateTicket(ORDER_ID));
    }

    @Test
    void cancelCreateTicket() {
        given()
            .commandHandlers(commandHandler.commandHandlers())
        .when()
            .receives(new CancelCreateTicket(ORDER_ID))
        .then()
            .verify((m) -> verify(kitchenService).cancelCreateTicket(ORDER_ID));
    }
}
