package me.jangjunha.ftgo.apigateway.routers.orders

import io.grpc.StatusRuntimeException
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import me.jangjunha.ftgo.apigateway.proxies.KitchenService
import me.jangjunha.ftgo.apigateway.proxies.OrderService
import me.jangjunha.ftgo.apigateway.proxies.RestaurantService
import me.jangjunha.ftgo.apigateway.security.grpc.ForwardCallCredentials
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.ReactiveSecurityContextHolder
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
        val securityContext = ReactiveSecurityContextHolder.getContext().awaitSingle()

        val orderId = UUID.fromString(request.pathVariable("orderId"))

        val order = async { orderService.findOrderById(orderId, ForwardCallCredentials(securityContext)) }
        val ticket = async {
            try {
                kitchenService.findTicketById(orderId, ForwardCallCredentials(securityContext))
            } catch (e: StatusRuntimeException) {
                null
            }
        }

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
