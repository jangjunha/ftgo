package me.jangjunha.ftgo.delivery_service.grpc

import me.jangjunha.ftgo.delivery_service.api.DeliveryServiceGrpcKt.DeliveryServiceCoroutineImplBase
import me.jangjunha.ftgo.delivery_service.domain.DeliveryService

class DeliveryServiceImpl(
    private val deliveryService: DeliveryService,
) : DeliveryServiceCoroutineImplBase()
