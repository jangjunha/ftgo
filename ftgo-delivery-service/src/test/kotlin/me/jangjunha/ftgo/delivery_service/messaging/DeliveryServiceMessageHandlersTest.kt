package me.jangjunha.ftgo.delivery_service.messaging

import io.eventuate.common.json.mapper.JSonMapper
import io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.ORDER_ID
import me.jangjunha.ftgo.delivery_service.DeliveryFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.delivery_service.domain.DeliveryService
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceChannels
import me.jangjunha.ftgo.kitchen_service.api.events.TicketAcceptedEvent
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import me.jangjunha.ftgo.order_service.api.events.OrderCreated
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.*

class DeliveryServiceMessageHandlersTest {
    private lateinit var deliveryService: DeliveryService
    private lateinit var messageHandlers: DeliveryServiceMessageHandlers

    @BeforeEach
    fun setUp() {
        JSonMapper.objectMapper.findAndRegisterModules()

        deliveryService = mockk()
        messageHandlers = DeliveryServiceMessageHandlers(deliveryService)
    }

    @Test
    fun createRestaurant() {
        every { deliveryService.upsertRestaurant(any(), any(), any()) } returns mockk()

        given()
            .eventHandlers(messageHandlers.domainEventHandlers())
        .`when`()
            .aggregate("me.jangjunha.ftgo.restaurant_service.domain.Restaurant", RESTAURANT_ID)
            .publishes(RestaurantCreated(
                "A Cafe",
                "서울시 강남구 테헤란로 1234",
                mutableListOf(),
            ))
        .then()
            .verify {
                verify { deliveryService.upsertRestaurant(
                    RESTAURANT_ID,
                    "A Cafe",
                    "서울시 강남구 테헤란로 1234"
                ) }
            }
    }

    @Test
    fun createOrder() {
        every { deliveryService.createDelivery(any(), any(), any()) } returns mockk()

        given()
            .eventHandlers(messageHandlers.domainEventHandlers())
        .`when`()
            .aggregate(OrderServiceChannels.ORDER_EVENT_CHANNEL, ORDER_ID)
            .publishes(OrderCreated(
                OrderDetails(
                    listOf(),
                    Money.ZERO,
                    RESTAURANT_ID,
                    UUID.fromString("0ddc83e1-79e6-4dc4-8fd2-35e7a0447d78"),
                ),
                "Seoul",
                "A Cafe",
            ))
        .then()
            .verify {
                verify { deliveryService.createDelivery(ORDER_ID, RESTAURANT_ID, "Seoul") }
            }
    }

    @Test
    fun acceptTicket() {
        every { deliveryService.scheduleDelivery(any(), any()) } returns mockk()

        given()
            .eventHandlers(messageHandlers.domainEventHandlers())
        .`when`()
            .aggregate(KitchenServiceChannels.TICKET_EVENT_CHANNEL, ORDER_ID)
            .publishes(TicketAcceptedEvent(OffsetDateTime.parse("2023-10-16T15:00Z")))
        .verify {
            verify { deliveryService.scheduleDelivery(ORDER_ID, OffsetDateTime.parse("2023-10-16T15:00Z")) }
        }
    }
}
