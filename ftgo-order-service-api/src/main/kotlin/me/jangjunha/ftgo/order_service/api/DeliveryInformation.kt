package me.jangjunha.ftgo.order_service.api

import java.time.OffsetDateTime

data class DeliveryInformation (
    var deliveryTime: OffsetDateTime = OffsetDateTime.MIN,
    var deliveryAddress: String = "",
)
