package me.jangjunha.ftgo.delivery_service.domain

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class DeliveryService
@Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
    private val deliveryRepository: DeliveryRepository,
) {

    fun upsertRestaurant(id: UUID, name: String, address: String) {
        restaurantRepository.save(Restaurant(id, name, address))
    }

    fun createDelivery(orderId: UUID, restaurantId: UUID, address: String) {
        val restaurant =
            restaurantRepository.findByIdOrNull(restaurantId) ?: throw RestaurantNotFoundException(restaurantId)
        deliveryRepository.save(
            Delivery(
                id = orderId,
                restaurantId = restaurantId,
                pickupAddress = restaurant.address,
                deliveryAddress = address,
            )
        )
    }
}
