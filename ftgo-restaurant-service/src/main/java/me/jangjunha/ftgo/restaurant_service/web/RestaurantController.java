package me.jangjunha.ftgo.restaurant_service.web;

import me.jangjunha.ftgo.restaurant_service.domain.CreateRestaurantRequest;
import me.jangjunha.ftgo.restaurant_service.domain.Restaurant;
import me.jangjunha.ftgo.restaurant_service.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/restaurants/")
public class RestaurantController {
    RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/")
    public CreateRestaurantResponse create(@RequestBody CreateRestaurantRequest request) {
        Restaurant restaurant = restaurantService.create(request);
        return new CreateRestaurantResponse(restaurant.getId());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/{restaurantId}/")
    public ResponseEntity<GetRestaurantResponse> get(@PathVariable UUID restaurantId) {
        return restaurantService.get(restaurantId)
                .map(r -> new ResponseEntity<>(new GetRestaurantResponse(r.getId(), r.getName(), r.getMenuItems()), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
