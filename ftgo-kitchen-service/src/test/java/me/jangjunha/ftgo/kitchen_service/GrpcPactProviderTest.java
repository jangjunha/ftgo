package me.jangjunha.ftgo.kitchen_service;

import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PluginTestTarget;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFilter;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector;
import io.eventuate.tram.spring.inmemory.TramInMemoryCommonConfiguration;
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.domain.Ticket;
import me.jangjunha.ftgo.kitchen_service.domain.TicketState;
import me.jangjunha.ftgo.kitchen_service.grpc.GrpcServer;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Provider("ftgo-kitchen-service")
@PactFilter(value = {"GRPC"}, filter = ByInteractionType.class)
@PactBroker
public class GrpcPactProviderTest {

    static final int PORT = 50011;

    private final KitchenService kitchenService = mock(KitchenService.class);

    private final GrpcServer grpcServer = new GrpcServer(PORT, kitchenService);

    @State("ticket which state is `AWAITING_ACCEPTANCE`")
    void toAwaitingAcceptanceTicketExists() {
        Ticket ticket = new Ticket(
                UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
                101L,
                new TicketDetails(List.of(
                        new TicketDetails.LineItem(2, "latte", "Cafe Latte")
                ))
        );
        ticket.setState(TicketState.AWAITING_ACCEPTANCE);
        when(kitchenService.getTicket(any())).thenReturn(ticket);
    }

    @State("ticket which state is `ACCEPTED`")
    void toAcceptedTicketExists() {
        Ticket ticket = new Ticket(
                UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
                101L,
                new TicketDetails(List.of(
                        new TicketDetails.LineItem(2, "latte", "Cafe Latte")
                ))
        );
        ticket.setState(TicketState.ACCEPTED);
        ticket.setAcceptTime(OffsetDateTime.parse("1970-01-01T00:00Z"));
        ticket.setReadyBy(OffsetDateTime.parse("1970-01-01T00:30Z"));
        when(kitchenService.getTicket(any())).thenReturn(ticket);
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void setUp(PactVerificationContext context) {
        grpcServer.start();

        Map<String, Object> pluginOptions = new HashMap<String, Object>();
        pluginOptions.put("host", "localhost");
        pluginOptions.put("port", PORT);
        pluginOptions.put("transport", "grpc");
        context.setTarget(new PluginTestTarget(pluginOptions));
    }

    @AfterEach
    void tearDown() {
        grpcServer.stop();
    }

    @Configuration
    @EnableAutoConfiguration
    @Import({
        NoopDuplicateMessageDetector.class,
        TramMessagingCommonAutoConfiguration.class,
        TramInMemoryCommonConfiguration.class
    })
    static class TestConfiguration {}
}
