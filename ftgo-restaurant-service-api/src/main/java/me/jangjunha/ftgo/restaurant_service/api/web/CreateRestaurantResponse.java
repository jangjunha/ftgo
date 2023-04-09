package me.jangjunha.ftgo.restaurant_service.api.web;

import java.util.UUID;

public class CreateRestaurantResponse {
    public UUID id;

    public CreateRestaurantResponse(UUID id) {
        this.id = id;
    }
}
