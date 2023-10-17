package me.jangjunha.ftgo.delivery_service.domain

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import me.jangjunha.ftgo.common.protobuf.TimestampUtils
import me.jangjunha.ftgo.delivery_service.api.CourierAction as CourierActionAPI
import me.jangjunha.ftgo.delivery_service.api.DeliveryActionType
import me.jangjunha.ftgo.delivery_service.api.courierAction
import java.time.OffsetDateTime
import java.util.UUID

@Embeddable
data class Action(
    @Enumerated(EnumType.STRING)
    val type: DeliveryActionType,
    val deliveryId: UUID,
    val address: String,
    val time: OffsetDateTime,
) {

    fun actionFor(deliveryId: UUID): Boolean = this.deliveryId == deliveryId

    fun serialize(): CourierActionAPI = courierAction {
        type = this@Action.type
        deliveryId = this@Action.deliveryId.toString()
        address = this@Action.address
        time = TimestampUtils.toTimestamp(this@Action.time)
    }

    companion object {
        fun makePickup(deliveryId: UUID, pickupAddress: String, pickupTime: OffsetDateTime) = Action(
            type = DeliveryActionType.PICKUP,
            deliveryId = deliveryId,
            address = pickupAddress,
            time = pickupTime,
        )

        fun makeDropoff(deliveryId: UUID, deliveryAddress: String, deliveryTime: OffsetDateTime) = Action(
            type = DeliveryActionType.DROPOFF,
            deliveryId = deliveryId,
            address = deliveryAddress,
            time = deliveryTime,
        )
    }
}
