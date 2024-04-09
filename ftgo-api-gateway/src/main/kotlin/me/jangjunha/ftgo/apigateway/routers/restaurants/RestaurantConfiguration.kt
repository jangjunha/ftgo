package me.jangjunha.ftgo.apigateway.routers.restaurants

import me.jangjunha.ftgo.apigateway.Destinations
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@EnableConfigurationProperties(Destinations::class)
class RestaurantConfiguration {

    @Bean
    fun restaurantHandlerRouting(restaurantHandlers: RestaurantHandlers): RouterFunction<ServerResponse> = coRouter {
        "/restaurants/".nest {
            GET("/", restaurantHandlers::getRestaurants)
        }
    }
}
