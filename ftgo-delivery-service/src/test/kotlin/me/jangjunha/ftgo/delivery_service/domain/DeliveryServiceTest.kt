package me.jangjunha.ftgo.delivery_service.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.COURIER_ID
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.DELIVERY_ID
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.delivery_service.api.DeliveryState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.data.repository.findByIdOrNull
import java.time.OffsetDateTime
import java.util.*

class DeliveryServiceTest {

    private lateinit var restaurantRepository: RestaurantRepository
    private lateinit var deliveryRepository: DeliveryRepository
    private lateinit var courierRepository: CourierRepository

    private lateinit var deliveryService: DeliveryService

    @BeforeEach
    fun setUp() {
        restaurantRepository = mockk()
        deliveryRepository = mockk()
        courierRepository = mockk()

        deliveryService = DeliveryService(
            restaurantRepository,
            deliveryRepository,
            courierRepository,
        )
    }

    @Test
    fun upsertRestaurant() {
        every { restaurantRepository.save(any()) } returns mockk()

        deliveryService.upsertRestaurant(
            UUID.fromString("b4254304-d199-4383-94bd-62121e68091a"),
            "A Cafe",
            "서울시 강남구 테헤란로 2",
        )

        verify {
            restaurantRepository.save(
                Restaurant(
                    UUID.fromString("b4254304-d199-4383-94bd-62121e68091a"),
                    "A Cafe",
                    "서울시 강남구 테헤란로 2",
                )
            )
        }
    }

    @Test
    fun getCourier() {
        val COURIER = Courier(
            id = COURIER_ID,
            available = true,
        )
        every { courierRepository.findByIdOrNull(COURIER_ID) } returns COURIER

        val courier = deliveryService.getCourier(COURIER_ID)
        assert(courier == COURIER)
    }

    @Test
    fun getNotExistingCourier() {
        every { courierRepository.findByIdOrNull(any()) } returns null

        assertThrows<CourierNotFoundException> {
            deliveryService.getCourier(COURIER_ID)
        }
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun updateCourierAvailability(isAvailable: Boolean) {
        val COURIER = Courier(
            id = COURIER_ID,
            available = false,
        )
        every { courierRepository.findByIdOrNull(COURIER_ID) } returns COURIER
        every { courierRepository.save(any()) } returns mockk()

        deliveryService.updateCourierAvailability(COURIER_ID, isAvailable)

        verify { courierRepository.save(COURIER.copy(available = isAvailable)) }
    }

    @Test
    fun getDelivery() {
        val DELIVERY = Delivery(
            id = DELIVERY_ID,
            restaurantId = RESTAURANT_ID,
            pickupAddress = "P",
            deliveryAddress = "D",
        )
        every { deliveryRepository.findByIdOrNull(DELIVERY_ID) } returns DELIVERY

        val delivery = deliveryService.getDelivery(DELIVERY_ID)
        assert(delivery == DELIVERY)
    }

    @Test
    fun getNotExistingDelivery() {
        every { deliveryRepository.findByIdOrNull(any()) } returns null

        assertThrows<DeliveryNotFoundException> {
            deliveryService.getDelivery(DELIVERY_ID)
        }
    }

    @Test
    fun createDelivery() {
        every { deliveryRepository.save(any()) } returns mockk()
        every { restaurantRepository.findByIdOrNull(RESTAURANT_ID) } returns Restaurant(
            id = RESTAURANT_ID,
            name = "Cafe",
            address = "Incheon",
        )

        deliveryService.createDelivery(DELIVERY_ID, RESTAURANT_ID, "Seoul")

        verify { deliveryRepository.save(Delivery(
            id = DELIVERY_ID,
            restaurantId = RESTAURANT_ID,
            pickupAddress = "Incheon",
            deliveryAddress = "Seoul",
        )) }
    }

    @Test
    fun scheduleDelivery() {
        val DELIVERY = Delivery(
            id = DELIVERY_ID,
            restaurantId = RESTAURANT_ID,
            pickupAddress = "P",
            deliveryAddress = "D",
        )
        every { deliveryRepository.findByIdOrNull(any()) } returns DELIVERY
        every { deliveryRepository.save(any()) } returns mockk()
        every { courierRepository.findAllAvailable() } returns listOf(
            Courier(id = COURIER_ID)
        )

        deliveryService.scheduleDelivery(
            DELIVERY_ID,
            OffsetDateTime.parse("2023-10-16T14:00+09:00")
        )

        verify {
            deliveryRepository.save(
                DELIVERY.copy(
                    assignedCourierId = COURIER_ID,
                    readyBy = OffsetDateTime.parse("2023-10-16T14:00+09:00"),
                    state = DeliveryState.SCHEDULED,
                )
            )
        }
    }
}
