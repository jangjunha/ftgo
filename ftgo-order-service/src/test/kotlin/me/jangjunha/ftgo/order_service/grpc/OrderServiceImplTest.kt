package me.jangjunha.ftgo.order_service.grpc

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.common.api.money
import me.jangjunha.ftgo.common.protobuf.TimestampUtils
import me.jangjunha.ftgo.order_service.OrderFixtures.CONSUMER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.DELIVERY_INFO
import me.jangjunha.ftgo.order_service.OrderFixtures.LATTE_LINE_ITEMS
import me.jangjunha.ftgo.order_service.OrderFixtures.ORDER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.order_service.api.*
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.domain.MenuItemIdAndQuantity
import me.jangjunha.ftgo.order_service.domain.Order
import me.jangjunha.ftgo.order_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_service.domain.OrderLineItems
import me.jangjunha.ftgo.order_service.service.OrderService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.util.*

class OrderServiceImplTest {
    private lateinit var orderService: OrderService
    private lateinit var orderServiceImpl: OrderServiceImpl

    companion object {
        val ORDER_1 = Order(
            ORDER_ID,
            2,
            OrderState.APPROVED,
            CONSUMER_ID,
            RESTAURANT_ID,
            OrderLineItems(LATTE_LINE_ITEMS.toMutableList()),
            DELIVERY_INFO,
        )
    }

    @BeforeEach
    fun setUp() {
        orderService = mockk()
        orderServiceImpl = OrderServiceImpl(orderService)

        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now() } returns OffsetDateTime.parse("2023-05-16T09:18+09:00")
    }

    @Test
    fun getOrder() = runBlocking {
        every { orderService.getOrder(ORDER_ID) } returns ORDER_1

        val payload = getOrderPayload {
            id = ORDER_ID.toString()
        }
        val order = orderServiceImpl.getOrder(payload)

        assertEquals(order {
            id = "43517ead-0606-4d49-98f9-6b6b873b944e"
            state = OrderState.APPROVED
            restaurantId = "d4420ba4-9fa4-4d8e-8e16-a750b9210e82"
            consumerId = "0763e858-6a8b-499b-9745-7fc230c54716"
            lineItems.addAll(listOf(
                orderLineItem {
                    menuItemId = "latte"
                    name = "Cafe Latte"
                    quantity = 1
                    price = money {
                        amount = "2500"
                    }
                }
            ))
            deliveryInformation = deliveryInformation {
                deliveryTime = TimestampUtils.toTimestamp(OffsetDateTime.parse("2019-11-24T12:30+09:00"))
                deliveryAddress = "서울시 강남구 테헤란로 1"
            }
            orderMinimum = money {
                amount = "2147483647"
            }
        }, order)
    }

    suspend fun createOrder() = runBlocking {
        every { orderService.createOrder(any(), any(), any(), any()) } returns Order(
            UUID.fromString("53c5be2f-591c-4900-ab0d-0546fb652731"),
            1,
            OrderState.APPROVAL_PENDING,
            UUID(0, 0),
            UUID(0, 0),
            OrderLineItems(mutableListOf(OrderLineItem(1, "", "", Money.ZERO))),
            DeliveryInformation(OffsetDateTime.MIN, ""),
        )

        val payload = createOrderPayload {
            restaurantId = RESTAURANT_ID.toString()
            consumerId = CONSUMER_ID.toString()
            items.addAll(listOf(
                menuItemIdAndQuantity {
                    menuItemId = "americano"
                    quantity = 1
                },
            ))
        }
        orderServiceImpl.createOrder(payload)

        verify {
            orderService.createOrder(
                UUID.fromString("8b7577db-68b6-4ef2-9212-5e406836c642"),
                UUID.fromString("8a6b3d42-03cc-4f15-980f-f0e00494bc4a"),
                listOf(MenuItemIdAndQuantity("americano", 2)),
                DeliveryInformation(
                    OffsetDateTime.parse("2023-05-16T10:18+09:00"),
                    "서울시 강남구 테헤란로 1",
                ),
            )
        }
    }
}
