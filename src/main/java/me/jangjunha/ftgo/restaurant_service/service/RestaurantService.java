package me.jangjunha.ftgo.restaurant_service.service;

import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.restaurant_service.domain.CreateRestaurantRequest;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.domain.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public Restaurant create(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant(request.name, request.menuItems);
        restaurantRepository.save(restaurant);
        return restaurant;
    }

    public Optional<Restaurant> get(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    public Iterable<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }
}
