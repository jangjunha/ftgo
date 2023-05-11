package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType

@Access(AccessType.FIELD)
data class PaymentInformation(
    var paymentToken: String,
)
