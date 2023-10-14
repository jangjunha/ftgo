package me.jangjunha.ftgo.restaurant_service.domain;

import me.jangjunha.ftgo.common.Money;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantTest {
    private static final MenuItem AMERICANO = makeMenuItem("americano", "Americano", new Money("1500"));
    private static final MenuItem LATTE = makeMenuItem("latte", "Cafe Latte", new Money("2500"));
    private static final Restaurant RESTAURANT = new Restaurant("Rem Cafe", "서울시 강남구 테헤란로 2", Arrays.asList(AMERICANO, LATTE));

    @Test
    void findMenuItem() {
        assertEquals(Optional.of(AMERICANO), RESTAURANT.findMenuItem("americano"));
        assertEquals(Optional.of(LATTE), RESTAURANT.findMenuItem("latte"));
        assertEquals(Optional.empty(), RESTAURANT.findMenuItem("strawberry-latte"));
    }

    private static MenuItem makeMenuItem(String id, String name, Money price) {
        MenuItem mi = new MenuItem();
        mi.setId(id);
        mi.setName(name);
        mi.setPrice(price);
        return mi;
    }
}
