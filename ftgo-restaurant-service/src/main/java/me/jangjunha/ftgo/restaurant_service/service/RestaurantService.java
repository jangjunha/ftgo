package me.jangjunha.ftgo.restaurant_service.service;

import jakarta.transaction.Transactional;
import me.jangjunha.ftgo.restaurant_service.api.MenuItem;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.domain.RestaurantAlreadyExistsException;
import me.jangjunha.ftgo.restaurant_service.domain.RestaurantRepository;
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated;
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

    public Restaurant create(Restaurant restaurant) {
        if (restaurant.getId() != null && restaurantRepository.existsById(restaurant.getId())) {
            throw new RestaurantAlreadyExistsException();
        }
        restaurantRepository.save(restaurant);
        restaurantDomainEventPublisher.publish(restaurant, Collections.singletonList(
                new RestaurantCreated(restaurant.getName(), restaurant.getAddress(), restaurant.getMenuItems().stream().map(m -> new MenuItem(m.getId(), m.getName(), m.getPrice())).toList())
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
