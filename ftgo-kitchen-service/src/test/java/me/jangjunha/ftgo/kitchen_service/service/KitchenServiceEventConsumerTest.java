package me.jangjunha.ftgo.kitchen_service.service;

import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.kitchen_service.domain.MenuItem;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given;
import static me.jangjunha.ftgo.kitchen_service.KitchenFixtures.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class KitchenServiceEventConsumerTest {

    private KitchenService kitchenService;
    private KitchenServiceEventConsumer eventConsumer;

    @BeforeEach
    void setUp() {
        kitchenService = mock(KitchenService.class);
        eventConsumer = new KitchenServiceEventConsumer(kitchenService);
    }

    @Test
    void restaurantCreated() {
        given()
            .aggregate("me.jangjunha.ftgo.restaurant_service.domain.Restaurant", A_CAFE_ID)
            .eventHandlers(eventConsumer.domainEventHandlers())
        .when()
            .publishes(new RestaurantCreated("Latte Cafe", "서울시 강남구 테헤란로 2", List.of(
                    new me.jangjunha.ftgo.restaurant_service.api.MenuItem("latte", "Cafe Latte", new Money("4000")),
                    new me.jangjunha.ftgo.restaurant_service.api.MenuItem("strawberry-latte", "Strawberry Latte", new Money("5500"))
            )))
        .then()
            .verify(() ->
                    verify(kitchenService).upsertRestaurant(A_CAFE_ID, List.of(
                            new MenuItem("latte", "Cafe Latte", new Money("4000")),
                            new MenuItem("strawberry-latte", "Strawberry Latte", new Money("5500"))
                    ))
            );
    }

    void restaurantMenuRevised() {
        given()
            .aggregate("me.jangjunha.ftgo.restaurant_service.domain.Restaurant", A_CAFE_ID)
            .eventHandlers(eventConsumer.domainEventHandlers())
        .when()
            .publishes(new RestaurantMenuRevised(List.of(
                    new me.jangjunha.ftgo.restaurant_service.api.MenuItem("mint-latte", "Mint Latte", new Money("5500"))
            )))
        .then()
            .verify(() ->
                    verify(kitchenService).upsertRestaurant(A_CAFE_ID, List.of(
                            new MenuItem("mint-latte", "Mint Latte", new Money("5500"))
                    ))
            );
    }
}
