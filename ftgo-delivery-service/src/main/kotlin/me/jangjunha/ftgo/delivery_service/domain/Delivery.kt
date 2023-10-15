package me.jangjunha.ftgo.delivery_service.domain

import jakarta.persistence.*
import me.jangjunha.ftgo.delivery_service.api.DeliveryState
import java.time.OffsetDateTime
import java.util.UUID

@Entity
@Access(AccessType.FIELD)
data class Delivery(
    @Id
    val id: UUID,

    val pickupAddress: String,

    @Enumerated(EnumType.STRING)
    val state: DeliveryState = DeliveryState.PENDING,

    val restaurantId: UUID,

    val pickupTime: OffsetDateTime? = null,

    val deliveryAddress: String,

    val deliveryTime: OffsetDateTime? = null,

    val assignedCourierId: UUID? = null,

    val readyBy: OffsetDateTime? = null,
) {

    fun scheduled(readyBy: OffsetDateTime, assignedCourier: UUID) = copy(
        state = DeliveryState.SCHEDULED,
        readyBy = readyBy,
        assignedCourierId = assignedCourier,
    )

    fun cancelled() = copy(
        state = DeliveryState.CANCELLED,
        assignedCourierId = null,
    )
}
