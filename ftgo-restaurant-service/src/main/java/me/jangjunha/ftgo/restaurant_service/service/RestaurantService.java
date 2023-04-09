package me.jangjunha.ftgo.restaurant_service.service;

import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.restaurant_service.domain.CreateRestaurantRequest;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.domain.RestaurantRepository;
import me.jangjunha.ftgo.restaurant_service.events.RestaurantCreated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantDomainEventPublisher restaurantDomainEventPublisher;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, RestaurantDomainEventPublisher restaurantDomainEventPublisher) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantDomainEventPublisher = restaurantDomainEventPublisher;
    }

    public Restaurant create(CreateRestaurantRequest request) {
        Restaurant restaurant = new Restaurant(request.name, request.menuItems);
        restaurantRepository.save(restaurant);
        restaurantDomainEventPublisher.publish(restaurant, Collections.singletonList(
                new RestaurantCreated(restaurant.getName(), restaurant.getMenuItems())
        ));
        return restaurant;
    }

    public Optional<Restaurant> get(UUID restaurantId) {
        return restaurantRepository.findById(restaurantId);
    }

    public Iterable<Restaurant> getAll() {
        return restaurantRepository.findAll();
    }
}
