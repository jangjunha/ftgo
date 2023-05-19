package me.jangjunha.ftgo.order_service.api

data class OrderRevision(
    val deliveryInformation: DeliveryInformation,
    val revisedOrderLineItems: List<RevisedOrderLineItem>,
)
