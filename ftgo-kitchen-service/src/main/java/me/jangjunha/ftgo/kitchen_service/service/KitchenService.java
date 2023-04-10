package me.jangjunha.ftgo.kitchen_service.service;

import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.kitchen_service.domain.MenuItem;
import me.jangjunha.ftgo.kitchen_service.domain.Restaurant;
import me.jangjunha.ftgo.kitchen_service.domain.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class KitchenService {
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public KitchenService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public void upsertRestaurant(UUID id, List<MenuItem> menuItems) {
        Restaurant restaurant = new Restaurant(id, menuItems);
        restaurantRepository.save(restaurant);
    }
}
