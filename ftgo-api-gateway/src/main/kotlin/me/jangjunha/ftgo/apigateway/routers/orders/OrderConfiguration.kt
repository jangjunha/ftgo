package me.jangjunha.ftgo.apigateway.routers.orders

import me.jangjunha.ftgo.apigateway.Destinations
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@EnableConfigurationProperties(Destinations::class)
class OrderConfiguration {

    @Bean
    fun orderProxyRouting(builder: RouteLocatorBuilder, destinations: Destinations): RouteLocator {
        return builder.routes()
            .route { it.path("/orders/").and().method(HttpMethod.POST).uri(destinations.orderServiceUrl) }
            .route { it.path("/orders/").and().method(HttpMethod.PUT).uri(destinations.orderServiceUrl) }
            .route { it.path("/orders/**").and().method(HttpMethod.POST).uri(destinations.orderServiceUrl) }
            .route { it.path("/orders/**").and().method(HttpMethod.PUT).uri(destinations.orderServiceUrl) }
            .route { it.path("/orders/").and().method(HttpMethod.GET).uri(destinations.orderHistoryServiceUrl) }
            .build()
    }

    @Bean
    fun orderHandlerRouting(orderHandlers: OrderHandlers): RouterFunction<ServerResponse> = coRouter {
        "/orders/".nest {
            GET("/{orderId}/", orderHandlers::getOrderDetails)
        }
    }
}
