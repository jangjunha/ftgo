package me.jangjunha.ftgo.order_history_service.web

import me.jangjunha.ftgo.common.auth.AuthenticatedClient
import me.jangjunha.ftgo.common.auth.AuthenticatedConsumerID
import me.jangjunha.ftgo.common.auth.AuthenticatedCourierID
import me.jangjunha.ftgo.common.auth.AuthenticatedID
import me.jangjunha.ftgo.common.auth.AuthenticatedRestaurantID
import me.jangjunha.ftgo.common.web.AuthContext
import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import me.jangjunha.ftgo.order_history_service.OrderHistoryFilter
import me.jangjunha.ftgo.order_history_service.domain.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
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
        @AuthContext authenticatedID: AuthenticatedID?,
    ): GetOrdersResponse {
        val authorized = when (authenticatedID) {
            is AuthenticatedClient -> true
            is AuthenticatedConsumerID -> authenticatedID.consumerId == consumerId
            is AuthenticatedRestaurantID, is AuthenticatedCourierID, null -> false
        }
        if (!authorized) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        val history = orderHistoryDAO.findOrderHistory(consumerId, filter)
        return GetOrdersResponse(
            history.orders.map(this::makeGetOrderResponse),
            history.startKey,
        )
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/{id}/"])
    fun getOrder(
        @PathVariable id: UUID,
        @AuthContext authenticatedID: AuthenticatedID?,
    ): GetOrderResponse {
        val order = orderHistoryDAO.findOrderById(id)
        val authorized = when (authenticatedID) {
            is AuthenticatedClient -> true
            is AuthenticatedConsumerID -> authenticatedID.consumerId == order.consumerId
            is AuthenticatedRestaurantID -> authenticatedID.restaurantId == order.restaurantId
            is AuthenticatedCourierID, null -> false
        }
        if (!authorized) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }

        return makeGetOrderResponse(order)
    }

    private fun makeGetOrderResponse(order: Order): GetOrderResponse {
        return GetOrderResponse(
            order.orderId,
            order.status,
            order.restaurantId,
            order.restaurantName,
            order.consumerId,
            order.creationDate,
            order.lineItems.map {
                GetOrderResponse.LineItem(
                    it.quantity,
                    it.menuItemId,
                    it.name,
                    it.price,
                )
            },
        )
    }
}
