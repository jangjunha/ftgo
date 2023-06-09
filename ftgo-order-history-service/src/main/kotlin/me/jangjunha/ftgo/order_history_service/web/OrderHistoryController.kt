package me.jangjunha.ftgo.order_history_service.web

import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import me.jangjunha.ftgo.order_history_service.OrderHistoryFilter
import me.jangjunha.ftgo.order_history_service.domain.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/orders/"])
class OrderHistoryController
@Autowired constructor(
    private val orderHistoryDAO: OrderHistoryDAO,
) {

    @RequestMapping(method = [RequestMethod.GET])
    fun getOrders(
        @RequestParam(name = "consumerId") consumerId: UUID,
        filter: OrderHistoryFilter,
    ): GetOrdersResponse {
        val history = orderHistoryDAO.findOrderHistory(consumerId, filter)
        return GetOrdersResponse(
            history.orders.map(this::makeGetOrderResponse),
            history.startKey,
        )
    }

    private fun makeGetOrderResponse(order: Order): GetOrderResponse {
        return GetOrderResponse(
            order.orderId,
            order.status,
            order.restaurantId,
            order.restaurantName,
        )
    }
}
