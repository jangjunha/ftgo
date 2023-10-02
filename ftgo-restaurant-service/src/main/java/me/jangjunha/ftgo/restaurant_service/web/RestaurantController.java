package me.jangjunha.ftgo.restaurant_service.web;

import me.jangjunha.ftgo.common.auth.AuthenticatedClient;
import me.jangjunha.ftgo.common.auth.AuthenticatedID;
import me.jangjunha.ftgo.common.auth.AuthenticatedRestaurantID;
import me.jangjunha.ftgo.common.web.AuthContext;
import me.jangjunha.ftgo.restaurant_service.api.MenuItem;
import me.jangjunha.ftgo.restaurant_service.api.web.CreateRestaurantResponse;
import me.jangjunha.ftgo.restaurant_service.api.web.GetRestaurantResponse;
import me.jangjunha.ftgo.restaurant_service.domain.CreateRestaurantRequest;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.domain.RestaurantAlreadyExistsException;
import me.jangjunha.ftgo.restaurant_service.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/restaurants/")
public class RestaurantController {
    RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/")
    public List<GetRestaurantResponse> list() {
        return StreamSupport.stream(restaurantService.getAll().spliterator(), false)
                .map(this::serialize)
                .toList();
    }

    @RequestMapping(method = RequestMethod.POST, path = "/")
    public CreateRestaurantResponse create(@RequestBody CreateRestaurantRequest request, @AuthContext AuthenticatedID authenticatedID) {
        if (!(authenticatedID instanceof AuthenticatedClient)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Restaurant restaurant = restaurantService.create(new Restaurant(request.name, request.menuItems));
        return new CreateRestaurantResponse(restaurant.getId());
    }

    @RequestMapping(method = RequestMethod.POST, path = "/{restaurantId}/")
    public CreateRestaurantResponse createWithId(@PathVariable UUID restaurantId, @RequestBody CreateRestaurantRequest request, @AuthContext AuthenticatedID authenticatedID) {
        if (authenticatedID instanceof AuthenticatedClient) {
        } else if (authenticatedID instanceof AuthenticatedRestaurantID && ((AuthenticatedRestaurantID) authenticatedID).getRestaurantId() == restaurantId) {
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Restaurant restaurant;
        try {
            restaurant = restaurantService.create(new Restaurant(restaurantId, request.name, request.menuItems));
        } catch (RestaurantAlreadyExistsException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "restaurant with id %s already exists".formatted(restaurantId));
        }
        return new CreateRestaurantResponse(restaurant.getId());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{restaurantId}/")
    public ResponseEntity<GetRestaurantResponse> get(@PathVariable UUID restaurantId) {
        return restaurantService.get(restaurantId)
                .map(r -> new ResponseEntity<>(serialize(r), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private GetRestaurantResponse serialize(Restaurant restaurant) {
        return new GetRestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getMenuItems().stream().map(m -> new MenuItem(m.getId(), m.getName(), m.getPrice())).toList()
        );
    }
}
