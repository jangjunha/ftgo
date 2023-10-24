package me.jangjunha.ftgo.delivery_service.domain

import io.eventuate.tram.events.publisher.DomainEventPublisher
import io.mockk.*
import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.COURIER_ID
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.DELIVERY_ID
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.delivery_service.api.DeliveryActionType
import me.jangjunha.ftgo.delivery_service.api.DeliveryState
import me.jangjunha.ftgo.delivery_service.api.events.DeliveryDropoff
import me.jangjunha.ftgo.delivery_service.api.events.DeliveryPickedUp
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceGrpcKt
import me.jangjunha.ftgo.kitchen_service.api.TicketState
import me.jangjunha.ftgo.kitchen_service.api.ticket
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
    private lateinit var kitchenService: KitchenServiceGrpcKt.KitchenServiceCoroutineStub
    private lateinit var domainEventPublisher: DomainEventPublisher

    private lateinit var deliveryService: DeliveryService

    private val NOW = OffsetDateTime.parse("2023-10-17T09:00Z")

    @BeforeEach
    fun setUp() {
        restaurantRepository = mockk()
        deliveryRepository = mockk()
        courierRepository = mockk()
        kitchenService = mockk()
        domainEventPublisher = mockk(relaxed = true)

        deliveryService = DeliveryService(
            restaurantRepository,
            deliveryRepository,
            courierRepository,
            kitchenService,
            domainEventPublisher,
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
        every { courierRepository.findByIdOrNullWithPlan(COURIER_ID) } returns COURIER

        val courier = deliveryService.getCourier(COURIER_ID)
        assert(courier == COURIER)
    }

    @Test
    fun getNotExistingCourier() {
        every { courierRepository.findByIdOrNullWithPlan(any()) } returns null

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
        val COURIER = Courier(id = COURIER_ID, available = true)
        every { deliveryRepository.findByIdOrNull(any()) } returns DELIVERY
        every { deliveryRepository.save(any()) } returns mockk()
        every { courierRepository.findAllAvailable() } returns listOf(COURIER)
        every { courierRepository.save(any()) } returns mockk()

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
        verify {
            courierRepository.save(
                COURIER.copy(plan = Plan(actions = listOf(
                    Action(DeliveryActionType.PICKUP, DELIVERY_ID, "P", OffsetDateTime.parse("2023-10-16T14:00+09:00")),
                    Action(DeliveryActionType.DROPOFF, DELIVERY_ID, "D", OffsetDateTime.parse("2023-10-16T14:30+09:00")),
                )))
            )
        }
    }

    @Test
    fun pickupDelivery() = runBlocking {
        val DELIVERY = Delivery(
            id = DELIVERY_ID,
            restaurantId = RESTAURANT_ID,
            assignedCourierId = COURIER_ID,
            pickupAddress = "P",
            deliveryAddress = "D",
        )
        every { deliveryRepository.findByIdOrNull(any()) } returns DELIVERY
        every { deliveryRepository.save(any()) } returns mockk()
        every { kitchenService.withCallCredentials(any()) } returns kitchenService
        coEvery { kitchenService.getTicket(any(), any()) } returns ticket {
            state = TicketState.READY_FOR_PICKUP
        }

        mockkStatic(OffsetDateTime::class) {
            every { OffsetDateTime.now() } returns NOW
            deliveryService.pickUpDelivery(DELIVERY_ID)
        }

        verify {
            deliveryRepository.save(
                DELIVERY.copy(pickupTime = NOW)
            )
        }
        verify {
            domainEventPublisher.publish(
                Delivery::class.java,
                DELIVERY_ID,
                listOf(
                    DeliveryPickedUp(NOW),
                ),
            )
        }
    }

    @Test
    fun dropoffDelivery() {
        val DELIVERY = Delivery(
            id = DELIVERY_ID,
            restaurantId = RESTAURANT_ID,
            assignedCourierId = COURIER_ID,
            pickupAddress = "P",
            deliveryAddress = "D",
        )
        val COURIER = Courier(
            id = COURIER_ID,
            available = true,
            plan = Plan(actions = listOf(
                Action(DeliveryActionType.PICKUP, DELIVERY_ID, "P", OffsetDateTime.MIN),
                Action(DeliveryActionType.DROPOFF, DELIVERY_ID, "D", OffsetDateTime.MIN),
            ))
        )
        every { deliveryRepository.findByIdOrNull(any()) } returns DELIVERY
        every { deliveryRepository.save(any()) } returns mockk()
        every { courierRepository.findByIdOrNull(any()) } returns COURIER
        every { courierRepository.save(any()) } returns mockk()

        mockkStatic(OffsetDateTime::class) {
            every { OffsetDateTime.now() } returns NOW
            deliveryService.dropoffDelivery(DELIVERY_ID)
        }

        verify {
            deliveryRepository.save(
                DELIVERY.copy(deliveryTime = NOW)
            )
        }
        verify {
            courierRepository.save(
                COURIER.copy(plan = Plan(actions = emptyList()))
            )
        }
        verify {
            domainEventPublisher.publish(
                Delivery::class.java,
                DELIVERY_ID,
                listOf(
                    DeliveryDropoff(NOW),
                ),
            )
        }
    }
}
