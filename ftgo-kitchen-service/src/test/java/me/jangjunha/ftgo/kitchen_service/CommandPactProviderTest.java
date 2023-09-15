package me.jangjunha.ftgo.kitchen_service;

import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.v4.MessageContents;
import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFilter;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import io.eventuate.tram.commands.consumer.CommandDispatcher;
import io.eventuate.tram.commands.consumer.CommandReplyProducer;
import io.eventuate.tram.commands.consumer.CommandReplyToken;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.common.MessageImpl;
import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration;
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails;
import me.jangjunha.ftgo.kitchen_service.domain.Ticket;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import me.jangjunha.ftgo.kitchen_service.service.KitchenServiceCommandHandler;
import me.jangjunha.ftgo.kitchen_service.service.KitchenServiceMessagingConfiguration;
import me.jangjunha.ftgo.kitchen_service.service.KitchenServiceEventConsumer;
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Provider("ftgo-kitchen-service")
@PactFilter(value = {"RawSynchronousMessage"}, filter = ByInteractionType.class)
@PactBroker
public class CommandPactProviderTest {

    @MockBean
    private KitchenService kitchenService;

    @SpyBean
    private CommandReplyProducer replyProducer;

    @SpyBean
    KitchenServiceCommandHandler kitchenServiceCommandHandler;

    @Autowired
    private CommandDispatcher commandDispatcher;

    @PactVerifyProvider("created ticket")
    public MessageAndMetadata verifyCreateTicket(V4Interaction.SynchronousMessages messages) {
        when(kitchenService.createTicket(any(), eq(UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35")), any())).thenReturn(new Ticket(
                UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
                101L,
                new TicketDetails(List.of(
                        new TicketDetails.LineItem(2, "americano", "Americano")
                ))
        ));

        ArgumentCaptor<CommandReplyToken> tokenArgumentCaptor = ArgumentCaptor.forClass(CommandReplyToken.class);
        ArgumentCaptor<List<Message>> messagesCaptor = ArgumentCaptor.forClass(List.class);

        Message commandMessage = buildMessage(messages.getRequest());
        commandDispatcher.messageHandler(commandMessage);

        verify(replyProducer).sendReplies(tokenArgumentCaptor.capture(), messagesCaptor.capture());
        Message replyMessage = messagesCaptor.getValue().get(0);
        return new MessageAndMetadata(replyMessage.getPayload().getBytes(), replyMessage.getHeaders());
    }

    @PactVerifyProvider("confirmed reply")
    public MessageAndMetadata verifyConfirmCreateTicket(V4Interaction.SynchronousMessages messages) {
        ArgumentCaptor<CommandReplyToken> tokenArgumentCaptor = ArgumentCaptor.forClass(CommandReplyToken.class);
        ArgumentCaptor<List<Message>> messagesCaptor = ArgumentCaptor.forClass(List.class);

        Message commandMessage = buildMessage(messages.getRequest());
        commandDispatcher.messageHandler(commandMessage);

        verify(replyProducer).sendReplies(tokenArgumentCaptor.capture(), messagesCaptor.capture());
        Message replyMessage = messagesCaptor.getValue().get(0);
        return new MessageAndMetadata(replyMessage.getPayload().getBytes(), replyMessage.getHeaders());
    }

    @PactVerifyProvider("cancelled reply")
    public MessageAndMetadata verifyCancelCreateTicket(V4Interaction.SynchronousMessages messages) {
        ArgumentCaptor<CommandReplyToken> tokenArgumentCaptor = ArgumentCaptor.forClass(CommandReplyToken.class);
        ArgumentCaptor<List<Message>> messagesCaptor = ArgumentCaptor.forClass(List.class);

        Message commandMessage = buildMessage(messages.getRequest());
        commandDispatcher.messageHandler(commandMessage);

        verify(replyProducer).sendReplies(tokenArgumentCaptor.capture(), messagesCaptor.capture());
        Message replyMessage = messagesCaptor.getValue().get(0);
        return new MessageAndMetadata(replyMessage.getPayload().getBytes(), replyMessage.getHeaders());
    }

    @State("'A Cafe' restaurant")
    void toRestaurantExists() {
    }

    @State("`CREATE_PENDING` ticket")
    void toCreatePendingTicketExists() {
    }

    private Message buildMessage(MessageContents contents) {
        return new MessageImpl(
                contents.getContents().valueAsString(),
                contents.getMetadata().entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().toString()
                ))
        );
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void testTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new MessageTestTarget());
    }

    @Configuration
    @EnableAutoConfiguration
    @Import({
            TramSagaInMemoryConfiguration.class,
            KitchenServiceMessagingConfiguration.class,
    })
    static class TestConfiguration {
        @Bean
        KitchenServiceEventConsumer kitchenServiceEventConsumer(KitchenService kitchenService) {
            return new KitchenServiceEventConsumer(kitchenService);
        }
    }
}
