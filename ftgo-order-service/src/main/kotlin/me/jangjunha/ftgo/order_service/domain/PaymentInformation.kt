package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Embeddable

@Embeddable
@Access(AccessType.FIELD)
data class PaymentInformation(
    var paymentToken: String,
)
