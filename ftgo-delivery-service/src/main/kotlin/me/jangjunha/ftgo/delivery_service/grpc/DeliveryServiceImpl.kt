package me.jangjunha.ftgo.delivery_service.grpc

import com.google.protobuf.Empty
import io.grpc.Status
import me.jangjunha.ftgo.common.auth.*
import me.jangjunha.ftgo.delivery_service.api.*
import me.jangjunha.ftgo.delivery_service.api.DeliveryServiceGrpcKt.DeliveryServiceCoroutineImplBase
import me.jangjunha.ftgo.delivery_service.domain.CourierNotFoundException
import me.jangjunha.ftgo.delivery_service.domain.DeliveryNotFoundException
import me.jangjunha.ftgo.delivery_service.domain.DeliveryService
import java.util.*

class DeliveryServiceImpl(
    private val deliveryService: DeliveryService,
) : DeliveryServiceCoroutineImplBase() {
    override suspend fun getDeliveryStatus(request: GetDeliveryStatusPayload): DeliveryStatus {
        val id = UUID.fromString(request.deliveryId)
        validatePermissionForDelivery(id)

        val delivery = try {
            deliveryService.getDelivery(id)
        } catch (e: DeliveryNotFoundException) {
            throw Status.NOT_FOUND.withCause(e).asRuntimeException()
        }
        val courier = delivery.assignedCourierId?.let {
            deliveryService.getCourier(it)
        }

        return deliveryStatus {
            deliveryInfo = deliveryInfo {
                this.id = delivery.id.toString()
                state = delivery.state
            }
            delivery.assignedCourierId?.also {
                assignedCourierId = it.toString()
            }
            courier?.also {
                courierActions.addAll(courier.actionsForDelivery(id).map {
                    actionInfo {
                        type = it.type
                    }
                })
            }
        }
    }

    override suspend fun updateCourierAvailability(request: UpdateCourierAvailabilityPayload): Empty {
        val id = UUID.fromString(request.courierId)
        validatePermissionForCourier(id)

        try {
            deliveryService.updateCourierAvailability(id, request.available)
        } catch (e: CourierNotFoundException) {
            throw Status.NOT_FOUND.withCause(e).asRuntimeException()
        }
        return Empty.newBuilder().build()
    }

    private fun validatePermissionForCourier(id: UUID) {
        val authenticatedID = AuthInterceptor.AUTHENTICATED_ID.get()
        val authenticated = when (authenticatedID) {
            is AuthenticatedClient -> true
            is AuthenticatedCourierID -> id == authenticatedID.courierId
            is AuthenticatedConsumerID, is AuthenticatedRestaurantID, null -> false
        }
        if (!authenticated) {
            throw Status.PERMISSION_DENIED.asRuntimeException()
        }
    }

    private fun validatePermissionForDelivery(deliveryId: UUID) {
        val authenticatedID = AuthInterceptor.AUTHENTICATED_ID.get()
        val authenticated = when (authenticatedID) {
            is AuthenticatedClient -> true
            is AuthenticatedCourierID -> {
                val delivery = deliveryService.getDelivery(deliveryId)
                delivery.assignedCourierId == authenticatedID.courierId
            }
            is AuthenticatedConsumerID, is AuthenticatedRestaurantID, null -> false
        }
        if (!authenticated) {
            throw Status.PERMISSION_DENIED.asRuntimeException()
        }
    }
}
