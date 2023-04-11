package me.jangjunha.ftgo.order_service.api.events

import me.jangjunha.ftgo.order_service.api.OrderDetails

data class OrderCreated(
    var orderDetails: OrderDetails,
    var deliveryAddress: String,
    var restaurantName: String
) : OrderDomainEvent
