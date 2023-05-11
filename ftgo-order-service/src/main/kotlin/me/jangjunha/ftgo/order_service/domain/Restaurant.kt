package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "restaurants")
@Access(AccessType.FIELD)
data class Restaurant(
    @Id
    var id: UUID,

    @Embedded
    @ElementCollection
    @CollectionTable(name = "restaurant_menu_items")
    var menuItems: MutableList<MenuItem>,
    var name: String
)
