package me.jangjunha.ftgo.restaurant_service;

import au.com.dius.pact.provider.MessageAndMetadata;
import au.com.dius.pact.provider.PactVerifyProvider;
import au.com.dius.pact.provider.junit5.MessageTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.junitsupport.loader.PactFilter;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import io.eventuate.common.json.mapper.JSonMapper;
import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType;
import me.jangjunha.ftgo.restaurant_service.api.MenuItem;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Provider("ftgo-restaurant-service")
@PactFilter(value = {"AsynchronousMessage", "Message"}, filter = ByInteractionType.class)
@PactBroker
public class MessagingPactProviderTest {

    @PactVerifyProvider("`RestaurantCreated` event")
    MessageAndMetadata restaurantCreatedEvent() {
        RestaurantCreated event = new RestaurantCreated(
                "A Cafe",
                "서울시 강남구 테헤란로 2",
                List.of(
                        new MenuItem("americano", "Americano", new Money("2500"))
                )
        );
        return new MessageAndMetadata(
                JSonMapper.toJson(event).getBytes(),
                Map.of(
                        "event-type", "me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated",
                        "event-aggregate-type", "me.jangjunha.ftgo.restaurant_service.domain.Restaurant",
                        "event-aggregate-id", "97e3c4c2-f336-4435-9314-ad1a633495df",
                        "ID", ""
                )
        );
    }

    @PactVerifyProvider("`RestaurantMenuRevised` event")
    MessageAndMetadata restaurantMenuRevisedEvent() {
        RestaurantMenuRevised event = new RestaurantMenuRevised(
                List.of(
                        new MenuItem("americano", "Americano", new Money("2500"))
                )
        );
        return new MessageAndMetadata(
                JSonMapper.toJson(event).getBytes(),
                Map.of(
                        "event-type", "me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised",
                        "event-aggregate-type", "me.jangjunha.ftgo.restaurant_service.domain.Restaurant",
                        "event-aggregate-id", "97e3c4c2-f336-4435-9314-ad1a633495df",
                        "ID", ""
                )
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
}
