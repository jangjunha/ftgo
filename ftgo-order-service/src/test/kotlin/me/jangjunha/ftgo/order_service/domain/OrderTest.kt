package me.jangjunha.ftgo.order_service.domain

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.api.events.OrderCreated
import me.jangjunha.ftgo.order_service.api.events.OrderDomainEvent
import me.jangjunha.ftgo.order_service.OrderFixtures.A_CAFE
import me.jangjunha.ftgo.order_service.OrderFixtures.CONSUMER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.DELIVERY_INFO
import me.jangjunha.ftgo.order_service.OrderFixtures.LATTE_LINE_ITEMS
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OrderTest {

    private lateinit var createResult: ResultWithDomainEvents<Order, OrderDomainEvent>
    private lateinit var order: Order

    @BeforeEach
    fun setUp() {
        createResult = Order.createOrder(CONSUMER_ID, A_CAFE, LATTE_LINE_ITEMS, DELIVERY_INFO)
        order = createResult.result
    }

    @Test
    fun shouldCreateOrder() {
        assertEquals(
            listOf(
                OrderCreated(
                    OrderDetails(
                        listOf(
                            OrderDetails.LineItem(
                                1,
                                "latte",
                                "Cafe Latte",
                                Money(2500),
                            ),
                        ),
                        Money(2500),
                        A_CAFE.id,
                        CONSUMER_ID,
                    ), "서울시 강남구 테헤란로 1", "A Cafe"
                )
            ), createResult.events
        )
        assertEquals(OrderState.APPROVAL_PENDING, order.state)
    }

    @Test
    fun shouldCalculateTotal() {
        assertEquals(Money(2500), order.orderLineItems.orderTotal)
    }
}
