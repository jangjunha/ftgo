package me.jangjunha.ftgo.order_service.web

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.verify
import jakarta.persistence.EntityManager
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.domain.*
import me.jangjunha.ftgo.order_service.service.OrderService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.OffsetDateTime
import java.util.UUID

@WebMvcTest
class OrderControllerTest
@Autowired constructor(
    val mockMvc: MockMvc,
) {
    @MockkBean
    lateinit var orderService: OrderService

    @MockkBean
    lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `Create Order`() {
        mockkStatic(OffsetDateTime::class)
        every { OffsetDateTime.now() } returns OffsetDateTime.parse("2023-05-16T09:18+09:00")
        every { orderService.createOrder(any(), any(), any(), any()) } returns Order(
            UUID.fromString("53c5be2f-591c-4900-ab0d-0546fb652731"),
            1,
            OrderState.APPROVAL_PENDING,
            UUID(0, 0),
            UUID(0, 0),
            OrderLineItems(mutableListOf(OrderLineItem(1, "", "", Money.ZERO))),
            DeliveryInformation(OffsetDateTime.MIN, ""),
        )

        mockMvc.perform(
            post("/orders/")
                .accept(MediaType.APPLICATION_JSON)
                .header("x-ftgo-authenticated-consumer-id", "8b7577db-68b6-4ef2-9212-5e406836c642")
                .content(
                    """
                    {
                        "restaurantId": "8a6b3d42-03cc-4f15-980f-f0e00494bc4a",
                        "consumerId": "8b7577db-68b6-4ef2-9212-5e406836c642",
                        "items": [
                            {"menuItemId": "americano", "quantity": 2}
                        ],
                        "deliveryAddress": "서울시 강남구 테헤란로 1"
                    }
                """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.orderId").value("53c5be2f-591c-4900-ab0d-0546fb652731"))
        val expectedDeliveryTime = OffsetDateTime.parse("2023-05-16T10:18+09:00")
        verify {
            orderService.createOrder(
                UUID.fromString("8b7577db-68b6-4ef2-9212-5e406836c642"),
                UUID.fromString("8a6b3d42-03cc-4f15-980f-f0e00494bc4a"),
                listOf(MenuItemIdAndQuantity("americano", 2)),
                DeliveryInformation(
                    expectedDeliveryTime,
                    "서울시 강남구 테헤란로 1",
                ),
            )
        }
    }
}
