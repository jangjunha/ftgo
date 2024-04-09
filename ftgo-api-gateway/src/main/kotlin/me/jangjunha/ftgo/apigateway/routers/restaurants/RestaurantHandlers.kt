package me.jangjunha.ftgo.apigateway.routers.restaurants

import me.jangjunha.ftgo.apigateway.proxies.RestaurantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait

@Component
class RestaurantHandlers
@Autowired constructor(
    private val restaurantService: RestaurantService,
) {

    suspend fun getRestaurants(request: ServerRequest): ServerResponse {
        val restaurants = restaurantService.listRestaurants()
        return ServerResponse.ok().bodyValueAndAwait(restaurants)
    }
}
