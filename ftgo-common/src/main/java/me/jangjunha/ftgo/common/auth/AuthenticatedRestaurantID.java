package me.jangjunha.ftgo.common.auth;


import java.util.UUID;

public final class AuthenticatedRestaurantID extends AuthenticatedID {

    private final UUID restaurantId;

    public AuthenticatedRestaurantID(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }
}