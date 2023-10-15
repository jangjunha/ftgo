package me.jangjunha.ftgo.delivery_service.domain

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface RestaurantRepository: CrudRepository<Restaurant, UUID>
