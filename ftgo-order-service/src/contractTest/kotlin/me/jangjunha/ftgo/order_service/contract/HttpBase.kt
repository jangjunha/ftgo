package me.jangjunha.ftgo.order_service.contract

import io.mockk.every
import io.mockk.mockk
import io.restassured.module.mockmvc.RestAssuredMockMvc
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.domain.Order
import me.jangjunha.ftgo.order_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_service.domain.OrderLineItems
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.order_service.web.OrderController
import org.junit.jupiter.api.BeforeEach
import java.time.OffsetDateTime
import java.util.UUID

abstract class HttpBase {

    private val orderService: OrderService = mockk()
    private val orderController = OrderController(orderService)

    companion object {
        private val ORDER_ID = UUID.fromString("56d58ad7-1e96-486d-b426-5d23e2e276ea")!!
        private val ORDER = Order(
            id = ORDER_ID,
            state = OrderState.APPROVAL_PENDING,
            consumerId = UUID.fromString("bbd1ae72-3323-4213-9f00-04df6fc4900f"),
            restaurantId = UUID.fromString("2a048668-4b29-456c-a798-6b2c4f6ba973"),
            orderLineItems = OrderLineItems(mutableListOf(
                OrderLineItem(2, "latte", "Cafe Latte", Money("2500"))
            )),
            deliveryInformation = DeliveryInformation(
                OffsetDateTime.parse("2023-08-14T14:00+09:00"),
                "서울시 강남구 테헤란로 1",
            ),
        )
    }

    @BeforeEach
    fun setUp() {
        every { orderService.createOrder(any(), any(), any(), any()) } returns ORDER

        RestAssuredMockMvc.standaloneSetup(orderController)
    }
}
