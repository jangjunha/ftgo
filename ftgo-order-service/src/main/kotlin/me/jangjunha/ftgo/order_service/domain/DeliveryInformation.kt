package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import java.time.OffsetDateTime

@Access(AccessType.FIELD)
data class DeliveryInformation (
    var deliveryTime: OffsetDateTime,
    var deliveryAddress: String,
)