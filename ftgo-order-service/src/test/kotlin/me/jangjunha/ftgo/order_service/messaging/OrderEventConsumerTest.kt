package me.jangjunha.ftgo.order_service.messaging

import io.eventuate.tram.testing.DomainEventHandlerUnitTestSupport.given
import io.mockk.mockk
import io.mockk.verify
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.OrderFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.order_service.domain.MenuItem
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.restaurant_service.api.MenuItem as RestaurantMenuItem
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantCreated
import me.jangjunha.ftgo.restaurant_service.api.events.RestaurantMenuRevised
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class OrderEventConsumerTest {
    lateinit var orderService: OrderService
    lateinit var orderEventConsumer: OrderEventConsumer

    @BeforeEach
    fun setUp() {
        orderService = mockk(relaxed = true)
        orderEventConsumer = OrderEventConsumer(orderService)
    }

    @Test
    fun createMenu() {
        given()
            .eventHandlers(orderEventConsumer.domainEventHandlers())
        .`when`()
            .aggregate("me.jangjunha.ftgo.restaurant_service.domain.Restaurant", RESTAURANT_ID)
            .publishes(
                RestaurantCreated(
                    "Latte Cafe",
                    listOf(
                        RestaurantMenuItem("latte", "Cafe Latte", Money("4000")),
                        RestaurantMenuItem("strawberry-latte", "Strawberry Latte", Money("5500")),
                    )
                )
            )
        .then()
            .verify {
                verify {
                    orderService.createMenu(RESTAURANT_ID, "Latte Cafe", listOf(
                        MenuItem("latte", "Cafe Latte", Money("4000")),
                        MenuItem("strawberry-latte", "Strawberry Latte", Money("5500")),
                    ))
                }
            }
    }

    @Test
    fun reviseMenu() {
        given()
            .eventHandlers(orderEventConsumer.domainEventHandlers())
            .`when`()
            .aggregate("me.jangjunha.ftgo.restaurant_service.domain.Restaurant", RESTAURANT_ID)
            .publishes(
                RestaurantMenuRevised(
                    listOf(
                        RestaurantMenuItem("latte", "Cafe Latte", Money("4000")),
                        RestaurantMenuItem("mint-chocolate-latte", "Mint Chocolate Latte", Money("6000")),
                    )
                )
            )
            .then()
            .verify {
                verify {
                    orderService.reviseMenu(RESTAURANT_ID, listOf(
                        MenuItem("latte", "Cafe Latte", Money("4000")),
                        MenuItem("mint-chocolate-latte", "Mint Chocolate Latte", Money("6000")),
                    ))
                }
            }
    }
}
