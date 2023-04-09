package me.jangjunha.ftgo.kitchen_service.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KitchenService {
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public KitchenService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
}
