package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Embeddable
import java.time.OffsetDateTime

@Embeddable
@Access(AccessType.FIELD)
data class DeliveryInformation (
    var deliveryTime: OffsetDateTime,
    var deliveryAddress: String,
)
