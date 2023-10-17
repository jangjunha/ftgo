package me.jangjunha.ftgo.kitchen_service.service;

import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.api.events.TicketCreatedEvent;
import me.jangjunha.ftgo.kitchen_service.domain.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.jangjunha.ftgo.kitchen_service.KitchenFixtures.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class KitchenServiceTest {

    private Restaurant aCafe;
    private Restaurant subway;
    private static final UUID TICKET_1_ID = new UUID(0, 1);
    private Ticket ticket1;

    MockedStatic<OffsetDateTime> mockedOffsetDateTime;
    private RestaurantRepository restaurantRepository;
    private TicketRepository ticketRepository;
    private TicketDomainEventPublisher domainEventPublisher;
    private KitchenService kitchenService;

    @BeforeEach
    void setUp() {
        ticket1 = makeTicket(TICKET_1_ID, TicketState.CREATE_PENDING);
        aCafe = new Restaurant(
                A_CAFE_ID,
                Arrays.asList(
                        new MenuItem("americano", "Americano", new Money("1500")),
                        new MenuItem("latte", "Cafe Latte", new Money("3500"))
                )
        );
        subway = new Restaurant(
                SUBWAY_ID,
                Arrays.asList(
                        new MenuItem("vegetables-15cm", "Vegetables (15cm)", new Money("5200")),
                        new MenuItem("blt-15cm", "BLT (15cm)", new Money("6700"))
                )
        );

        mockedOffsetDateTime = mockStatic(OffsetDateTime.class, CALLS_REAL_METHODS);
        mockedOffsetDateTime.when(OffsetDateTime::now).thenReturn(NOW);

        restaurantRepository = mock(RestaurantRepository.class);
        when(restaurantRepository.findAll()).thenReturn(Arrays.asList(aCafe, subway));
        when(restaurantRepository.findById(any())).thenReturn(Optional.empty());
        when(restaurantRepository.findById(A_CAFE_ID)).thenReturn(Optional.of(aCafe));
        when(restaurantRepository.findById(SUBWAY_ID)).thenReturn(Optional.of(subway));
        when(restaurantRepository.existsById(any())).thenReturn(false);
        when(restaurantRepository.existsById(A_CAFE_ID)).thenReturn(true);
        when(restaurantRepository.existsById(SUBWAY_ID)).thenReturn(true);

        ticketRepository = mock(TicketRepository.class);
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket1));
        when(ticketRepository.findById(any())).thenReturn(Optional.empty());
        when(ticketRepository.findById(TICKET_1_ID)).thenReturn(Optional.of(ticket1));

        domainEventPublisher = mock(TicketDomainEventPublisher.class);

        kitchenService = new KitchenService(domainEventPublisher, ticketRepository, restaurantRepository);
    }

    @AfterEach
    void tearDown() {
        mockedOffsetDateTime.close();
    }

    @Test
    void getTicket() {
        Ticket t1 = kitchenService.getTicket(TICKET_1_ID);
        assertEquals(ticket1, t1);

        assertThrows(TicketNotFoundException.class, () -> kitchenService.getTicket(new UUID(123, 456)));
    }

    @Test
    void createTicket() {
        when(ticketRepository.getLastSequence(A_CAFE_ID)).thenReturn(100L);

        Ticket createdTicket = kitchenService.createTicket(A_CAFE_ID, TICKET_1_ID, new TicketDetails(Arrays.asList(
                new TicketDetails.LineItem(2, "latte", "Cafe Latte"),
                new TicketDetails.LineItem(1, "americano", "Americano")
        )));

        assertEquals(ticket1, createdTicket);
        verify(ticketRepository).save(createdTicket);
        verify(domainEventPublisher).publish(createdTicket, List.of());
    }

    @Test
    void shouldNotCreateTicket() {
        assertThrows(
                RestaurantDetailsVerificationException.class,
                () -> kitchenService.createTicket(
                        new UUID(123, 456),  // NOT existing restaurant
                        TICKET_1_ID,
                        new TicketDetails()
                )
        );
    }

    @Test
    void confirmCreateTicket() {
        kitchenService.confirmCreateTicket(TICKET_1_ID);

        verify(domainEventPublisher).publish(ticket1, List.of(
                new TicketCreatedEvent(TICKET_1_ID, new TicketDetails(Arrays.asList(
                        new TicketDetails.LineItem(2, "latte", "Cafe Latte"),
                        new TicketDetails.LineItem(1, "americano", "Americano")
                )))
        ));
    }

    @Test
    void cancelCreateTicket() {
        kitchenService.cancelCreateTicket(TICKET_1_ID);

        assertEquals(ticket1.getState(), TicketState.CANCELLED);
        verify(domainEventPublisher).publish(ticket1, List.of());
    }

    @Test
    void accept() {
        ticket1.setState(TicketState.AWAITING_ACCEPTANCE);

        OffsetDateTime readyBy = OffsetDateTime.parse("2023-01-01T01:22+09:00");
        kitchenService.accept(TICKET_1_ID, readyBy);

        assertEquals(TicketState.ACCEPTED, ticket1.getState());
        assertEquals(readyBy, ticket1.getReadyBy());
    }

    @Test
    void pickUpTicket() {
        OffsetDateTime pickedUpTime = OffsetDateTime.parse("2023-02-01T04:00Z");
        kitchenService.pickUpTicket(TICKET_1_ID, pickedUpTime);

        assertEquals(pickedUpTime, ticket1.getPickedUpTime());
    }

    @Test
    void upsertRestaurant() {
        kitchenService.upsertRestaurant(A_CAFE_ID, List.of(
                new MenuItem("cheese-ball-cutlet", "치즈볼카츠", new Money("13900"))
        ));

        verify(restaurantRepository).save(new Restaurant(A_CAFE_ID, List.of(
                new MenuItem("cheese-ball-cutlet", "치즈볼카츠", new Money("13900"))
        )));
    }
}
