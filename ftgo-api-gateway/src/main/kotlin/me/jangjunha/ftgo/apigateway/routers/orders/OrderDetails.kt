package me.jangjunha.ftgo.apigateway.routers.orders

import me.jangjunha.ftgo.apigateway.entities.kitchens.TicketInfo
import me.jangjunha.ftgo.apigateway.entities.restaurants.RestaurantInfo
import me.jangjunha.ftgo.common.protobuf.TimestampUtils.*
import me.jangjunha.ftgo.kitchen_service.api.Ticket
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.restaurant_service.api.Restaurant
import java.time.OffsetDateTime
import java.util.UUID

data class OrderDetails(
    val id: UUID,
    val state: OrderState,
    val lineItems: List<LineItem>,

    val deliveryAddress: String,
    val deliveryTime: OffsetDateTime,

    val restaurant: RestaurantInfo,

    val ticket: TicketInfo?,
) {

    companion object {
        fun from(order: Order, restaurant: Restaurant, ticket: Ticket?): OrderDetails {
            return OrderDetails(
                id = UUID.fromString(order.id),
                state = order.state,
                lineItems = order.lineItemsList.map { li ->
                    LineItem(
                        quantity = li.quantity,
                        menuItemId = li.menuItemId,
                        name = li.name,
                    )
                },
                deliveryAddress = order.deliveryInformation.deliveryAddress,
                deliveryTime = fromTimestamp(order.deliveryInformation.deliveryTime),
                restaurant = RestaurantInfo.from(restaurant),
                ticket = ticket?.let { TicketInfo.from(it) },
            )
        }
    }

    data class LineItem(
        val quantity: Int,
        val menuItemId: String,
        val name: String,
    )
}
