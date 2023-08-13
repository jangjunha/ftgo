package me.jangjunha.ftgo.kitchen_service.domain;

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents;
import me.jangjunha.ftgo.common.UnsupportedStateTransitionException;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketAcceptedEvent;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketCreatedEvent;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketDomainEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

class TicketTest {

    private static final OffsetDateTime NOW = OffsetDateTime.parse("2023-01-01T00:00+09:00");
    private static final UUID TICKET_ID = UUID.fromString("d1e0e47c-fe8f-4e32-9513-74504f1a6723");
    private static final UUID RESTAURANT_ID = UUID.fromString("f4e39a80-5d86-4df0-b41a-2040e97e8cd9");
    private static final UUID CONSUMER_ID = UUID.fromString("4ac56572-fd35-4014-b1a7-8aec227657a0");

    private Ticket ticket;

    MockedStatic<OffsetDateTime> mockedOffsetDateTime;

    @BeforeEach
    void setUp() {
        mockedOffsetDateTime = mockStatic(OffsetDateTime.class, CALLS_REAL_METHODS);
        mockedOffsetDateTime.when(OffsetDateTime::now).thenReturn(NOW);

        ticket = new Ticket();
        ticket.setId(TICKET_ID);
        ticket.setState(TicketState.CREATE_PENDING);
        ticket.setRestaurantId(RESTAURANT_ID);
        ticket.setSequence(101L);
        ticket.setLineItems(Arrays.asList(
                new TicketLineItem(1, "latte", "Cafe Latte"),
                new TicketLineItem(2, "ice-americano", "Americano (iced)")
        ));
    }

    @AfterEach
    void tearDown() {
        mockedOffsetDateTime.close();
    }

    @Test
    void create() {
        ResultWithDomainEvents<Ticket, TicketDomainEvent> rwe = Ticket.create(TICKET_ID, RESTAURANT_ID, 101L, new TicketDetails(Arrays.asList(
                new TicketDetails.LineItem(1, "latte", "Cafe Latte"),
                new TicketDetails.LineItem(2, "ice-americano", "Americano (iced)")
        )));

        assertEquals(ticket, rwe.result);
        assertEquals(List.of(), rwe.events);
    }

    @Test
    void confirmCreate() {
        List<TicketDomainEvent> events = ticket.confirmCreate();

        assertEquals(TicketState.AWAITING_ACCEPTANCE, ticket.getState());
        assertThat(events).hasSameElementsAs(List.of(
                new TicketCreatedEvent(TICKET_ID, new TicketDetails(Arrays.asList(
                        new TicketDetails.LineItem(1, "latte", "Cafe Latte"),
                        new TicketDetails.LineItem(2, "ice-americano", "Americano (iced)")
                )))
        ));
    }

    @ParameterizedTest
    @EnumSource(TicketState.class)
    void shouldNotConfirmCreate(TicketState initialState) {
        ticket.setState(initialState);

        Consumer<Executable> assertion = switch (initialState) {
            case CREATE_PENDING -> Assertions::assertDoesNotThrow;
            case AWAITING_ACCEPTANCE, ACCEPTED, PREPARING, READY_FOR_PICKUP, PICKED_UP, CANCEL_PENDING, CANCELLED, REVISION_PENDING ->
                    exec ->
                            assertThrows(UnsupportedStateTransitionException.class, exec);
        };
        assertion.accept(() -> ticket.confirmCreate());
    }

    @Test
    void cancelCreate() {
        ticket.setState(TicketState.CREATE_PENDING);

        List<TicketDomainEvent> events = ticket.cancelCreate();
        assertEquals(TicketState.CANCELLED, ticket.getState());
        assertThat(events).hasSameElementsAs(List.of());
    }

    @ParameterizedTest
    @EnumSource(TicketState.class)
    void shouldNotCancelCreate(TicketState initialState) {
        ticket.setState(initialState);

        Consumer<Executable> assertion = switch (initialState) {
            case CREATE_PENDING -> Assertions::assertDoesNotThrow;
            case AWAITING_ACCEPTANCE, ACCEPTED, PREPARING, READY_FOR_PICKUP, PICKED_UP, CANCEL_PENDING, CANCELLED, REVISION_PENDING ->
                    exec ->
                            assertThrows(UnsupportedStateTransitionException.class, exec);
        };
        assertion.accept(() -> ticket.cancelCreate());
    }

    @Test
    void accept() {
        ticket.setState(TicketState.AWAITING_ACCEPTANCE);
        OffsetDateTime readyBy = OffsetDateTime.parse("2023-01-01T15:26:13+09:00");

        List<TicketDomainEvent> events = ticket.accept(readyBy);

        assertEquals(TicketState.ACCEPTED, ticket.getState());
        assertEquals(readyBy, ticket.getReadyBy());
        assertThat(events).hasSameElementsAs(List.of(new TicketAcceptedEvent(readyBy)));
    }

    @ParameterizedTest
    @MethodSource("shouldNotAcceptReadyByGenerator")
    void shouldNotAcceptReadyBy(OffsetDateTime readyBy) {
        ticket.setState(TicketState.AWAITING_ACCEPTANCE);

        assertThrows(IllegalArgumentException.class, () -> ticket.accept(readyBy));
    }

    private static Stream<OffsetDateTime> shouldNotAcceptReadyByGenerator() {
        return Stream.of(
                OffsetDateTime.parse("2022-12-31T23:59:59+09:00"),
                OffsetDateTime.parse("2023-01-01T00:00:00+09:00")
        );
    }

    @ParameterizedTest
    @EnumSource(TicketState.class)
    void shouldNotAccept(TicketState initialState) {
        ticket.setState(initialState);
        OffsetDateTime readyBy = OffsetDateTime.parse("2023-01-01T15:26:13+09:00");

        Consumer<Executable> assertion = switch (initialState) {
            case AWAITING_ACCEPTANCE -> Assertions::assertDoesNotThrow;
            case ACCEPTED -> exec ->
                    assertThrows(AlreadyAcceptedException.class, exec);
            case CREATE_PENDING, PREPARING, READY_FOR_PICKUP, PICKED_UP, CANCEL_PENDING, CANCELLED, REVISION_PENDING ->
                    exec ->
                            assertThrows(UnsupportedStateTransitionException.class, exec);
        };
        assertion.accept(() -> ticket.accept(readyBy));
    }

    @Test
    void cancel() {
        ticket.setState(TicketState.ACCEPTED);

        List<TicketDomainEvent> events = ticket.cancel();
        assertEquals(TicketState.CANCEL_PENDING, ticket.getState());
        assertThat(events).hasSameElementsAs(List.of());
    }

    @ParameterizedTest
    @EnumSource(TicketState.class)
    void shouldNotCancel(TicketState initialState) {
        ticket.setState(initialState);

        Consumer<Executable> assertion = switch (initialState) {
            case AWAITING_ACCEPTANCE, ACCEPTED -> Assertions::assertDoesNotThrow;
            case CREATE_PENDING, PREPARING, READY_FOR_PICKUP, PICKED_UP, CANCEL_PENDING, CANCELLED, REVISION_PENDING ->
                    exec ->
                            assertThrows(UnsupportedStateTransitionException.class, exec);
        };
        assertion.accept(() -> ticket.cancel());
    }
}
