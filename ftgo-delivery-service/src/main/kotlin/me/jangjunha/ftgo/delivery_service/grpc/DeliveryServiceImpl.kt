package me.jangjunha.ftgo.delivery_service.grpc

import com.google.protobuf.Empty
import io.grpc.Status
import me.jangjunha.ftgo.common.auth.*
import me.jangjunha.ftgo.common.protobuf.TimestampUtils
import me.jangjunha.ftgo.delivery_service.api.*
import me.jangjunha.ftgo.delivery_service.api.Courier
import me.jangjunha.ftgo.delivery_service.api.DeliveryServiceGrpcKt.DeliveryServiceCoroutineImplBase
import me.jangjunha.ftgo.delivery_service.domain.*
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.beans.factory.annotation.Autowired
import java.util.*


@GrpcService(interceptors = [AuthInterceptor::class])
class DeliveryServiceImpl
@Autowired constructor(
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
                delivery.pickupTime?.also {
                    pickupTime = TimestampUtils.toTimestamp(it)
                }
                delivery.deliveryTime?.also {
                    deliveryTime = TimestampUtils.toTimestamp(it)
                }
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

    override suspend fun pickupDelivery(request: PickupDeliveryPayload): Empty {
        val id = UUID.fromString(request.deliveryId)
        validatePermissionForDelivery(id)

        try {
            deliveryService.pickUpDelivery(id)
        } catch (e: InvalidPreconditionException) {
            throw Status.FAILED_PRECONDITION
                .withDescription("Delivery is not ready for pickup")
                .withCause(e)
                .asRuntimeException()
        } catch (e: AlreadyPerformedException) {
            throw Status.ALREADY_EXISTS
                .withDescription("Delivery was already picked up")
                .withCause(e)
                .asRuntimeException()
        }
        return Empty.newBuilder().build()
    }

    override suspend fun dropoffDelivery(request: DropoffDeliveryPayload): Empty {
        val id = UUID.fromString(request.deliveryId)
        validatePermissionForDelivery(id)

        deliveryService.dropoffDelivery(id)
        return Empty.newBuilder().build()
    }

    override suspend fun createConrier(request: Empty): Courier {
        val courier = deliveryService.createCourier()
        return courier.serialize()
    }

    override suspend fun getCourier(request: GetCourierPayload): Courier {
        val id = UUID.fromString(request.courierId)
        validatePermissionForCourier(id)

        val courier = deliveryService.getCourier(id)
        return courier.serialize()
    }

    override suspend fun getCourierPlan(request: GetCourierPayload): CourierPlan {
        val id = UUID.fromString(request.courierId)
        validatePermissionForCourier(id)

        val courier = deliveryService.getCourier(id)
        return courier.plan.serialize()
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
