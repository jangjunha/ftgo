package me.jangjunha.ftgo.delivery_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import me.jangjunha.ftgo.delivery_service.api.courier
import me.jangjunha.ftgo.delivery_service.api.Courier as CourierAPI
import java.util.UUID

@Entity
@Access(AccessType.FIELD)
data class Courier(
    @Id
    val id: UUID,

    @Embedded
    val plan: Plan = Plan(),

    val available: Boolean = false,
) {

    fun actionAdded(action: Action) = copy(plan = plan.added(action))

    fun deliveryDone(deliveryId: UUID) = copy(plan = plan.removedDelivery(deliveryId))

    fun deliveryCancelled(deliveryId: UUID) = copy(plan = plan.removedDelivery(deliveryId))

    fun actionsForDelivery(deliveryId: UUID) = plan.actionsForDelivery(deliveryId)

    fun notedAvailable() = copy(available = true)

    fun notedUnavailable() = copy(available = false)

    fun serialize(): CourierAPI = courier {
        id = this@Courier.id.toString()
        available = this@Courier.available
    }
}
