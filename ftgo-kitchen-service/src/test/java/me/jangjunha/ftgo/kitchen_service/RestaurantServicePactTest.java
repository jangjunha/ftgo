package me.jangjunha.ftgo.kitchen_service;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.PactSpecVersion;
import au.com.dius.pact.core.model.V4Interaction;
import au.com.dius.pact.core.model.V4Pact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.eventuate.tram.events.subscriber.DomainEventDispatcher;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.messaging.common.MessageImpl;
import io.eventuate.tram.spring.consumer.common.TramNoopDuplicateMessageDetectorConfiguration;
import io.eventuate.tram.spring.inmemory.TramInMemoryCommonConfiguration;
import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.kitchen_service.domain.MenuItem;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import me.jangjunha.ftgo.kitchen_service.service.KitchenServiceMessagingConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@ExtendWith(PactConsumerTestExt.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@PactTestFor(providerName = "ftgo-restaurant-service", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V4)
public class RestaurantServicePactTest {

    @MockBean
    private KitchenService kitchenService;

    @Autowired
    DomainEventDispatcher domainEventDispatcher;

    @Pact(consumer = "ftgo-kitchen-service")
    V4Pact restaurantCreated(MessagePactBuilder builder) {
        return builder
                .expectsToReceive("`RestaurantCreated` event")
                .withMetadata(
                        Map.of(
                            "event-aggregate-type", "me.jangjunha.ftgo.restaurant_service.domain.Restaurant",
                            "event-type", "me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated",
                            "event-aggregate-id", "97e3c4c2-f336-4435-9314-ad1a633495df",
                            "ID", ""
                        )
                )
                .withContent(new PactDslJsonBody()
                        .stringType("name", "A Cafe")
                        .eachLike("menuItems")
                            .stringType("id", "americano")
                            .stringType("name", "Americano")
                            .object("price", new PactDslJsonBody().numberType("amount", 2500))
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "restaurantCreated")
    void testConsumeRestaurantCreated(V4Interaction.AsynchronousMessage interaction) {
        Message message = new MessageImpl(interaction.contentsAsString(), interaction.getMetadata().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().toString()
        )));
        domainEventDispatcher.messageHandler(message);

        verify(kitchenService).upsertRestaurant(
                eq(UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df")),
                eq(List.of(
                        new MenuItem("americano", "Americano", new Money("2500"))
                ))
        );
    }

    @Pact(consumer = "ftgo-kitchen-service")
    V4Pact restaurantMenuRevised(MessagePactBuilder builder) {
        return builder
                .expectsToReceive("`RestaurantMenuRevised` event")
                .withMetadata(
                        Map.of(
                                "event-aggregate-type", "me.jangjunha.ftgo.restaurant_service.domain.Restaurant",
                                "event-type", "me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised",
                                "event-aggregate-id", "97e3c4c2-f336-4435-9314-ad1a633495df",
                                "ID", ""
                        )
                )
                .withContent(new PactDslJsonBody()
                        .eachLike("menuItems")
                        .stringType("id", "americano")
                        .stringType("name", "Americano")
                        .object("price", new PactDslJsonBody().numberType("amount", 2500))
                )
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "restaurantMenuRevised")
    void testConsumeRestaurantMenuRevised(V4Interaction.AsynchronousMessage interaction) {
        Message message = new MessageImpl(interaction.contentsAsString(), interaction.getMetadata().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().toString()
        )));
        domainEventDispatcher.messageHandler(message);

        verify(kitchenService).upsertRestaurant(
                eq(UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df")),
                eq(List.of(
                        new MenuItem("americano", "Americano", new Money("2500"))
                ))
        );
    }

    @Configuration
    @EnableAutoConfiguration
    @Import({
            KitchenServiceMessagingConfiguration.class,
            TramInMemoryCommonConfiguration.class,
            TramNoopDuplicateMessageDetectorConfiguration.class,
    })
    static class TestConfiguration {
    }
}
