package me.jangjunha.ftgo.kitchen_service.domain;

import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = JpaTestConfiguration.class)
public class KitchenJpaTest {

    private static UUID ID = UUID.fromString("446584e9-bc81-45c5-beb8-188ed6022ad2");
    private static UUID RESTAURANT_ID = UUID.fromString("61239045-95eb-4085-8857-dce2ceaba43c");

    @Autowired TicketRepository ticketRepository;
    @Autowired TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        ticketRepository.save(new Ticket(
                new UUID(0, 1),
                RESTAURANT_ID,
                100L,
                new TicketDetails(List.of())
        ));
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
    }

    @Test
    void shouldGetLastSequence() {
        long lastSequence = ticketRepository.getLastSequence(RESTAURANT_ID);
        assertEquals(100L, lastSequence);
    }

    @Test
    void shouldCreateTicket() {
        UUID ticketId = transactionTemplate.execute((ts) -> {
            Ticket t = ticketRepository.save(new Ticket(
                    ID,
                    RESTAURANT_ID,
                    101L,
                    new TicketDetails(List.of())
            ));
            return t.getId();
        });

        transactionTemplate.executeWithoutResult((ts) -> {
            Ticket ticket = ticketRepository.findById(ticketId).get();
            assertEquals(ID, ticket.getId());
            assertEquals(TicketState.CREATE_PENDING, ticket.getState());
            assertEquals(101L, ticket.getSequence());
            assertEquals(RESTAURANT_ID, ticket.getRestaurantId());
        });
    }
}
