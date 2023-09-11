package me.jangjunha.ftgo.order_service.domain

import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.support.TransactionTemplate
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [JpaTestConfiguration::class])
class OrderJpaTest
@Autowired constructor(
    private val orderRepository: OrderRepository,
    private val transactionTemplate: TransactionTemplate,
) {
    @Test
    fun `Should save and load Order`() {
        val orderId = transactionTemplate.execute {
            val order = orderRepository.save(
                Order(
                    id = ID,
                    version = 2,
                    state = OrderState.APPROVAL_PENDING,
                    consumerId = CONSUMER_ID,
                    restaurantId = RESTAURANT_ID,
                    orderLineItems = OrderLineItems(
                        mutableListOf(
                            OrderLineItem(1, "latte", "Cafe Latte", Money(3500)),
                        )
                    ),
                    deliveryInformation = DeliveryInformation(
                        deliveryTime = OffsetDateTime.parse("2023-08-12T10:05+09:00"),
                        deliveryAddress = "서울시 강남구 테헤란로 1",
                    )
                )
            )
            order.id
        }!!

        transactionTemplate.executeWithoutResult {
            val order = orderRepository.findById(orderId).get()
            assertEquals(2, order.version)
            assertEquals(OrderState.APPROVAL_PENDING, order.state)
            assertEquals(CONSUMER_ID, order.consumerId)
            assertEquals(RESTAURANT_ID, order.restaurantId)
            assertEquals(
                OrderLineItems(
                    mutableListOf(
                        OrderLineItem(1, "latte", "Cafe Latte", Money("3500.00")),
                    )
                ), order.orderLineItems
            )
            assertEquals(
                DeliveryInformation(
                    deliveryTime = OffsetDateTime.parse("2023-08-12T10:05+09:00"),
                    deliveryAddress = "서울시 강남구 테헤란로 1",
                ), order.deliveryInformation
            )
        }
    }

    companion object {
        val ID = UUID.fromString("be8a5f47-3947-4d15-be35-4c079577d360")!!
        val CONSUMER_ID = UUID.fromString("5c6fe86f-e4f8-4f30-b4e1-cfea991d5298")!!
        val RESTAURANT_ID = UUID.fromString("4e84b28a-11ee-4fb0-94a9-ac3990b72220")!!
    }
}
