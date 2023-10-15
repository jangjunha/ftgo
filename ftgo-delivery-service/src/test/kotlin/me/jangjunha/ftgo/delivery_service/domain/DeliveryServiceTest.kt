package me.jangjunha.ftgo.delivery_service.domain

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class DeliveryServiceTest {

    private lateinit var restaurantRepository: RestaurantRepository

    private lateinit var deliveryService: DeliveryService

    @BeforeEach
    fun setUp() {
        restaurantRepository = mockk()

        deliveryService = DeliveryService(
            restaurantRepository,
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

        verify { restaurantRepository.save(Restaurant(
            UUID.fromString("b4254304-d199-4383-94bd-62121e68091a"),
            "A Cafe",
            "서울시 강남구 테헤란로 2",
        )) }
    }
}
