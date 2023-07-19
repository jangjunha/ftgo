package me.jangjunha.ftgo.apigateway.routers.orders

import kotlinx.coroutines.*
import me.jangjunha.ftgo.apigateway.proxies.KitchenService
import me.jangjunha.ftgo.apigateway.proxies.OrderService
import me.jangjunha.ftgo.apigateway.proxies.RestaurantService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.*

@Component
class OrderHandlers
@Autowired constructor(
    private val kitchenService: KitchenService,
    private val orderService: OrderService,
    private val restaurantService: RestaurantService,
) {
    suspend fun getOrderDetails(request: ServerRequest): ServerResponse = coroutineScope {
        val orderId = UUID.fromString(request.pathVariable("orderId"))

        val order = async { orderService.findOrderById(orderId) }
        val ticket = async { kitchenService.findTicketById(orderId) }

        val restaurantId = UUID.fromString(order.await().restaurantId)
        val restaurant = async { restaurantService.findRestaurantById(restaurantId) }

        val orderDetails = OrderDetails.from(
            order.await(),
            restaurant.await(),
            ticket.await(),
        )
        ServerResponse.ok().bodyValueAndAwait(orderDetails)
    }
}
