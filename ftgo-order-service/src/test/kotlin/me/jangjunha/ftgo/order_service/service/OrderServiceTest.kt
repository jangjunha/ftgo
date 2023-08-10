package me.jangjunha.ftgo.order_service.service

import com.ninjasquad.springmockk.MockkBean
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.OrderFixtures.A_CAFE
import me.jangjunha.ftgo.order_service.OrderFixtures.CONSUMER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.DELIVERY_INFO
import me.jangjunha.ftgo.order_service.OrderFixtures.ORDER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.api.events.OrderCreated
import me.jangjunha.ftgo.order_service.domain.*
import me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga
import me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSagaState
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.util.*

class OrderServiceTest {

    @MockkBean
    lateinit var restaurantRepository: RestaurantRepository
    lateinit var orderRepository: OrderRepository
    lateinit var orderAggregateEventPublisher: OrderDomainEventPublisher
    lateinit var sagaInstanceFactory: SagaInstanceFactory
    lateinit var createOrderSaga: CreateOrderSaga

    lateinit var orderService: OrderService

    @BeforeEach
    fun setUp() {
        restaurantRepository = mockk<RestaurantRepository>()
        orderRepository = mockk<OrderRepository>()
        orderAggregateEventPublisher = mockk<OrderDomainEventPublisher>(relaxed = true)
        sagaInstanceFactory = mockk<SagaInstanceFactory>(relaxed = true)
        createOrderSaga = mockk<CreateOrderSaga>()

        val orderSlot = slot<Order>()
        every { restaurantRepository.findById(RESTAURANT_ID) } returns Optional.of(A_CAFE)
        every { orderRepository.save(capture(orderSlot)) } answers {
            orderSlot.captured.copy(id = ORDER_ID)
        }

        orderService = OrderService(
            restaurantRepository,
            orderRepository,
            orderAggregateEventPublisher,
            sagaInstanceFactory,
            createOrderSaga
        )
    }

    @Test
    fun createOrder() {
        val order = orderService.createOrder(
            CONSUMER_ID,
            RESTAURANT_ID,
            listOf(
                MenuItemIdAndQuantity("americano", 1),
                MenuItemIdAndQuantity("latte", 2),
            ),
            DELIVERY_INFO,
        )

        val expectedOrderDetails = OrderDetails(
            listOf(
                OrderDetails.LineItem(
                    1,
                    "americano",
                    "Americano",
                    Money("1500"),
                ),
                OrderDetails.LineItem(
                    2,
                    "latte",
                    "Cafe Latte",
                    Money("2500"),
                )
            ),
            Money("6500"),
            RESTAURANT_ID,
            CONSUMER_ID,
        )
        verify { orderRepository.save(order.copy(id = UUID(0, 0))) }
        verify {
            orderAggregateEventPublisher.publish(
                order,
                listOf(
                    OrderCreated(
                        expectedOrderDetails,
                        "서울시 강남구 테헤란로 1",
                        "A Cafe",
                    )
                )
            )
        }
        verify {
            sagaInstanceFactory.create(
                createOrderSaga, CreateOrderSagaState(
                    ORDER_ID,
                    expectedOrderDetails,
                    null,
                )
            )
        }
    }
}
