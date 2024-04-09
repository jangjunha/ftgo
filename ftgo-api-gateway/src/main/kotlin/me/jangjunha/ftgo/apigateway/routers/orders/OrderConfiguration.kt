package me.jangjunha.ftgo.apigateway.routers.orders

import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.apigateway.security.web.AuthenticatedHeaderFilter
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
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
    fun orderProxyRouting(builder: RouteLocatorBuilder, destinations: Destinations): RouteLocator = builder.routes {
        route {
            path("/orders/") and method(HttpMethod.POST)
            filters { filter(AuthenticatedHeaderFilter) }
            uri(destinations.orderServiceUrl)
        }
        route {
            path("/orders/") and method(HttpMethod.PUT)
            filters { filter(AuthenticatedHeaderFilter) }
            uri(destinations.orderServiceUrl)
        }
        route {
            path("/orders/**") and method(HttpMethod.POST)
            filters { filter(AuthenticatedHeaderFilter) }
            uri(destinations.orderServiceUrl)
        }
        route {
            path("/orders/**") and method(HttpMethod.PUT)
            filters { filter(AuthenticatedHeaderFilter) }
            uri(destinations.orderServiceUrl)
        }
        route {
            path("/orders/") and method(HttpMethod.GET)
            filters { filter(AuthenticatedHeaderFilter) }
            uri(destinations.orderHistoryServiceUrl)
        }
    }

    @Bean
    fun orderHandlerRouting(orderHandlers: OrderHandlers): RouterFunction<ServerResponse> = coRouter {
        "/orders/".nest {
            GET("/{orderId}/", orderHandlers::getOrderDetails)
        }
    }
}
