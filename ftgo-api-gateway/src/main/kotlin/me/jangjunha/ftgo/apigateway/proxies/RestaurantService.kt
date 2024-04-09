package me.jangjunha.ftgo.apigateway.proxies

import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.restaurant_service.api.Restaurant
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.net.URI
import java.util.UUID

@Service
class RestaurantService
@Autowired constructor(
    val destinations: Destinations,
    val client: WebClient,
) {
    suspend fun listRestaurants(): List<Restaurant> = client
        .get()
        .uri(URI(destinations.restaurantServiceUrl).resolve("./restaurants/"))
        .awaitExchange { response ->
            when (response.statusCode()) {
                HttpStatus.OK -> response.awaitBody<List<Restaurant>>()
                else -> throw RuntimeException("Cannot retrieve restaurant list")
            }
        }

    suspend fun findRestaurantById(id: UUID): Restaurant = client
        .get()
        .uri(URI(destinations.restaurantServiceUrl).resolve("./restaurants/$id/"))
        .awaitExchange { response ->
            when (response.statusCode()) {
                HttpStatus.OK -> response.awaitBody(Restaurant::class)
                HttpStatus.NOT_FOUND -> throw RestaurantNotFoundException(id)
                else -> throw RuntimeException("Cannot retrieve restaurant")
            }
        }
}
