package me.jangjunha.ftgo.apigateway.routers.kitchens

import me.jangjunha.ftgo.apigateway.Destinations
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.coRouter

@Configuration
@EnableConfigurationProperties(Destinations::class)
class KitchenConfiguration {

    @Bean
    fun kitchenHandlerRouting(kitchenHandlers: KitchenHandlers): RouterFunction<ServerResponse> = coRouter {
        "/tickets/".nest {
            "/{ticketId}/".nest {
                GET("/", kitchenHandlers::getTicket)
                GET("/accept/", kitchenHandlers::acceptTicket)
            }
        }
    }
}
