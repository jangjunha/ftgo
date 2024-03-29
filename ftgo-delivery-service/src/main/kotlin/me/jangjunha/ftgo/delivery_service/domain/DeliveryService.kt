package me.jangjunha.ftgo.delivery_service.domain

import io.eventuate.tram.events.publisher.DomainEventPublisher
import jakarta.transaction.Transactional
import me.jangjunha.ftgo.delivery_service.grpc.ClientCallCredentials
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceGrpcKt
import me.jangjunha.ftgo.kitchen_service.api.TicketState
import me.jangjunha.ftgo.kitchen_service.api.getTicketPayload
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.time.OffsetDateTime
import java.util.UUID


@Service
class DeliveryService
@Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
    private val deliveryRepository: DeliveryRepository,
    private val courierRepository: CourierRepository,
    private val kitchenService: KitchenServiceGrpcKt.KitchenServiceCoroutineStub,
    private val domainEventPublisher: DomainEventPublisher,
) {

    fun upsertRestaurant(id: UUID, name: String, address: String) {
        restaurantRepository.save(Restaurant(id, name, address))
    }

    fun getCourier(id: UUID): Courier =
        courierRepository.findByIdOrNullWithPlan(id) ?: throw CourierNotFoundException(id)

    fun createCourier(): Courier = courierRepository.save(Courier(id = UUID.randomUUID()))

    fun updateCourierAvailability(id: UUID, isAvailable: Boolean) {
        val courier = courierRepository.findByIdOrNull(id) ?: throw CourierNotFoundException(id)
        courierRepository.save(
            if (isAvailable) {
                courier.notedAvailable()
            } else {
                courier.notedUnavailable()
            }
        )
    }

    fun getDelivery(id: UUID): Delivery = deliveryRepository.findByIdOrNull(id) ?: throw DeliveryNotFoundException(id)

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
        courierRepository.save(
            courier
                .actionAdded(Action.makePickup(delivery.id, delivery.pickupAddress, readyBy))
                .actionAdded(Action.makeDropoff(delivery.id, delivery.deliveryAddress, readyBy.plusMinutes(30)))
        )
        deliveryRepository.save(
            delivery.scheduled(readyBy = readyBy, assignedCourier = courier.id)
        )
    }

    @Transactional
    suspend fun pickUpDelivery(id: UUID) {
        val now = OffsetDateTime.now()
        val delivery = deliveryRepository.findByIdOrNull(id) ?: throw DeliveryNotFoundException(id)

        if (delivery.pickupTime != null) {
            throw AlreadyPerformedException("Already picked up at ${delivery.pickupTime}")
        }
        val ticket = kitchenService
            .withCallCredentials(ClientCallCredentials())
            .getTicket(getTicketPayload { ticketId = id.toString() })
        if (ticket.state != TicketState.READY_FOR_PICKUP) {
            throw InvalidPreconditionException("Delivery $id is not ready for pickup")
        }

        val re = delivery.pickedUp(now)
        deliveryRepository.save(re.result)
        domainEventPublisher.publish(Delivery::class.java, id, re.events)
    }

    @Transactional
    fun dropoffDelivery(id: UUID) {
        val now = OffsetDateTime.now()
        val delivery = deliveryRepository.findByIdOrNull(id) ?: throw DeliveryNotFoundException(id)
        val courierId = delivery.assignedCourierId
            ?: throw RuntimeException("Trying to dropoff delivery which is no courier assigned")

        if (delivery.deliveryTime != null) {
            throw AlreadyPerformedException("Already dropped off at ${delivery.deliveryTime}")
        }

        val courier = courierRepository.findByIdOrNull(courierId)!!

        val re = delivery.dropoff(now)
        deliveryRepository.save(re.result)
        courierRepository.save(courier.deliveryDone(id))
        domainEventPublisher.publish(Delivery::class.java, id, re.events)
    }
}
