package me.jangjunha.ftgo.order_service.domain

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RestaurantRepository: CrudRepository<Restaurant, UUID> {}
