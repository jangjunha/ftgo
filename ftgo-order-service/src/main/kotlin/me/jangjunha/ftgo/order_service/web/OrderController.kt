package me.jangjunha.ftgo.order_service.web

import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime

@RestController
@RequestMapping(path = ["/orders/"])
class OrderController @Autowired constructor(
    private val orderService: OrderService
) {
    @RequestMapping(method = [RequestMethod.POST])
    fun createOrder(@RequestBody body: CreateOrderRequest): CreateOrderResponse {
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
