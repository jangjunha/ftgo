package me.jangjunha.ftgo.delivery_service.domain

import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID


@Service
class DeliveryService
@Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
    private val deliveryRepository: DeliveryRepository,
    private val courierRepository: CourierRepository,
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

    @Transactional
    fun scheduleDelivery(orderId: UUID, readyBy: OffsetDateTime) {
        val delivery = deliveryRepository.findByIdOrNull(orderId) ?: throw DeliveryNotFoundException(orderId)
        val courier = courierRepository.findAllAvailable().random()

        deliveryRepository.save(
            delivery.scheduled(readyBy = readyBy, assignedCourier = courier.id)
        )
    }
}
