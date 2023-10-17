package me.jangjunha.ftgo.kitchen_service.service;

import io.eventuate.common.json.mapper.JSonMapper;
import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.delivery_service.api.DeliveryServiceChannels;
import me.jangjunha.ftgo.delivery_service.api.events.DeliveryPickedUp;
import me.jangjunha.ftgo.kitchen_service.domain.MenuItem;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
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
        JSonMapper.objectMapper.findAndRegisterModules();

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

    @Test
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

    @Test
    void deliveryPickedUp() {
        given()
                .aggregate(DeliveryServiceChannels.DELIVERY_EVENT_CHANNEL, TICKET_ID)
                .eventHandlers(eventConsumer.domainEventHandlers())
        .when()
                .publishes(new DeliveryPickedUp(OffsetDateTime.parse("2023-01-01T00:00Z")))
        .then()
                .verify(() ->
                        verify(kitchenService).pickUpTicket(TICKET_ID, OffsetDateTime.parse("2023-01-01T00:00Z"))
                );
    }
}
