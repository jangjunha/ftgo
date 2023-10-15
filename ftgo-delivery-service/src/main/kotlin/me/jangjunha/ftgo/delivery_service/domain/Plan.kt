package me.jangjunha.ftgo.delivery_service.domain

import jakarta.persistence.ElementCollection
import java.util.UUID

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
}
