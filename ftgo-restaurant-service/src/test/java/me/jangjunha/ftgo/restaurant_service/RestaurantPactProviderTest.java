package me.jangjunha.ftgo.restaurant_service;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.loader.PactBroker;
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider;
import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.restaurant_service.domain.MenuItem;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.datasource.url=jdbc:h2:mem:db",
                "spring.datasource.driver-class-name=org.h2.Driver",
        }
)
@Provider("ftgo-restaurant-service")
@PactBroker(url = "https://pact.ftgo.jangjunha.me/")
public class RestaurantPactProviderTest {

    @LocalServerPort
    int port;

    @MockBean
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State({
            "'A Cafe' restaurant exist",
            "I have a restaurant",
    })
    void toACafeExistState() {
        when(
                restaurantService.get(eq(UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df")))
        ).thenReturn(
                Optional.of(A_CAFE)
        );
    }

    private static final UUID A_CAFE_ID = UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df");
    private static final Restaurant A_CAFE = makeRestaurant(
            A_CAFE_ID,
            "A Cafe",
            List.of(
                    makeMenuItem("americano", "Americano", new Money("2500"))
            )
    );

    private static Restaurant makeRestaurant(UUID id, String name, List<MenuItem> items) {
        Restaurant r = new Restaurant(name, items);
        r.setId(id);
        return r;
    }

    private static MenuItem makeMenuItem(String id, String name, Money price) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setName(name);
        item.setPrice(price);
        return item;
    }
}
