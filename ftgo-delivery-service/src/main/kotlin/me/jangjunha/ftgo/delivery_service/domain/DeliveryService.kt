package me.jangjunha.ftgo.delivery_service.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class DeliveryService
@Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
) {

    fun upsertRestaurant(id: UUID, name: String, address: String) {
        restaurantRepository.save(Restaurant(id, name, address))
    }
}
