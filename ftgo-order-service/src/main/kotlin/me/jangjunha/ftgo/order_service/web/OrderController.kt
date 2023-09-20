package me.jangjunha.ftgo.order_service.web

import me.jangjunha.ftgo.common.auth.AuthenticatedConsumerID
import me.jangjunha.ftgo.common.auth.AuthenticatedID
import me.jangjunha.ftgo.common.web.AuthContext
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.OffsetDateTime

@RestController
@RequestMapping(path = ["/orders/"])
class OrderController @Autowired constructor(
    private val orderService: OrderService
) {
    @RequestMapping(method = [RequestMethod.POST])
    fun createOrder(
        @AuthContext authenticatedID: AuthenticatedID?,
        @RequestBody body: CreateOrderRequest,
    ): CreateOrderResponse {
        if (!(authenticatedID is AuthenticatedConsumerID && authenticatedID.consumerId == body.consumerId)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val deliveryInformation = DeliveryInformation(
            OffsetDateTime.now().plusMinutes(60),
            body.deliveryAddress,
        )
        val order = orderService.createOrder(
            body.consumerId,
            body.restaurantId,
            body.items,
            deliveryInformation,
        )
        return CreateOrderResponse(order.id)
    }
}
