package me.jangjunha.ftgo.restaurant_service.service;

import me.jangjunha.ftgo.common.Money;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
import me.jangjunha.ftgo.restaurant_service.domain.MenuItem;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.domain.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantServiceTest {

    private static final UUID A_CAFE_ID = UUID.fromString("36cbd518-1001-4ed7-bf30-65fd6e576b36");
    private static final Restaurant A_CAFE = makeRestaurant(
            A_CAFE_ID,
            "A Cafe",
            "서울시 강남구 테헤란로 2",
            Arrays.asList(
                    makeMenuItem("americano", "Americano", new Money("1500")),
                    makeMenuItem("latte", "Cafe Latte", new Money("2500"))
            )
    );
    private static final UUID INE_RESTAURANT_ID = UUID.fromString("80f5b9a2-02d1-4418-b8c7-dc868152b3e8");
    private static final Restaurant INE_RESTAURANT = makeRestaurant(
            INE_RESTAURANT_ID,
            "INE Restaurant",
            "서울시 강남구 테헤란로 2",
            Arrays.asList(
                    makeMenuItem("omelet", "Omelet", new Money("12000")),
                    makeMenuItem("coke", "Coke", new Money("1500"))
            )
    );

    RestaurantRepository restaurantRepository;
    RestaurantDomainEventPublisher restaurantDomainEventPublisher;
    RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        restaurantRepository = mock(RestaurantRepository.class);
        when(
                restaurantRepository.findAll()
        ).thenReturn(Arrays.asList(A_CAFE, INE_RESTAURANT));
        when(
                restaurantRepository.findById(A_CAFE_ID)
        ).thenReturn(
                Optional.of(A_CAFE)
        );
        when(
                restaurantRepository.findById(INE_RESTAURANT_ID)
        ).thenReturn(
                Optional.of(INE_RESTAURANT)
        );

        restaurantDomainEventPublisher = mock(RestaurantDomainEventPublisher.class);

        restaurantService = new RestaurantService(restaurantRepository, restaurantDomainEventPublisher);
    }

    @Test
    void create() {
        Restaurant restaurant = restaurantService.create(new Restaurant(
                "Subway",
                "서울시 강남구 테헤란로 2",
                Arrays.asList(
                        makeMenuItem("meatball-15cm", "Meatball (15cm)", new Money("6700")),
                        makeMenuItem("blt-15cm", "BLT (15cm)", new Money("7300"))
                )
        ));

        verify(restaurantRepository).save(same(restaurant));
        verify(restaurantDomainEventPublisher).publish(restaurant, List.of(
                new RestaurantCreated("Subway", "서울시 강남구 테헤란로 2", Arrays.asList(
                        new me.jangjunha.ftgo.restaurant_service.api.MenuItem("meatball-15cm", "Meatball (15cm)", new Money("6700")),
                        new me.jangjunha.ftgo.restaurant_service.api.MenuItem("blt-15cm", "BLT (15cm)", new Money("7300"))
                )))
        );
    }

    @Test
    void get() {
        Optional<Restaurant> r1 = restaurantService.get(INE_RESTAURANT_ID);
        assertEquals(Optional.of(INE_RESTAURANT), r1);

        Optional<Restaurant> r2 = restaurantService.get(new UUID(1, 2));
        assertEquals(Optional.empty(), r2);
    }

    @Test
    void getAll() {
        Iterable<Restaurant> restaurants = restaurantService.getAll();
        assertThat(restaurants).hasSameElementsAs(Arrays.asList(A_CAFE, INE_RESTAURANT));
    }

    private static Restaurant makeRestaurant(UUID id, String name, String address, List<MenuItem> items) {
        Restaurant r = new Restaurant(name, address, items);
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
