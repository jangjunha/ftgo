package me.jangjunha.ftgo.delivery_service.domain

import jakarta.persistence.ElementCollection
import java.util.UUID
import me.jangjunha.ftgo.delivery_service.api.courierPlan
import me.jangjunha.ftgo.delivery_service.api.CourierPlan as CourierPlanAPI

data class Plan(
    @ElementCollection
    val actions: List<Action> = emptyList(),
) {
    fun added(action: Action) = Plan(
       actions + listOf(action)
    )

    fun removedDelivery(deliveryId: UUID) = Plan(
        actions.filter { !it.actionFor(deliveryId) }
    )

    fun actionsForDelivery(deliveryId: UUID): List<Action> = actions.filter { it.actionFor(deliveryId) }

    fun serialize(): CourierPlanAPI = courierPlan {
        actions.addAll(this@Plan.actions.map(Action::serialize))
    }
}
