package me.jangjunha.ftgo.delivery_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.UUID

@Entity
@Access(AccessType.FIELD)
data class Restaurant(
    @Id
    val id: UUID,
    val name: String,
    val address: String,
)
