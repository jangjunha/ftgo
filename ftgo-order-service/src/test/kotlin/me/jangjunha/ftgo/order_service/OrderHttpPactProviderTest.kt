package me.jangjunha.ftgo.order_service

import au.com.dius.pact.provider.junit5.HttpTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactFilter
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import com.ninjasquad.springmockk.MockkBean
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.inmemory.TramInMemoryCommonConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import io.mockk.every
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.domain.Order
import me.jangjunha.ftgo.order_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_service.domain.OrderLineItems
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.order_service.web.OrderController
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("ftgo-order-service")
@PactFilter(value = ["Http", "V4Http"], filter = ByInteractionType::class)
@PactBroker
class OrderHttpPactProviderTest {

    @LocalServerPort
    var port = 0

    @MockkBean
    lateinit var orderService: OrderService

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = HttpTestTarget("localhost", port)
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @State("a restaurant and a consumer")
    fun toRestaurantExistsState() {
        every { orderService.createOrder(any(), any(), any(), any()) } returns Order(
            id = UUID.randomUUID(),
            restaurantId = UUID.randomUUID(),
            consumerId = UUID.randomUUID(),
            deliveryInformation = DeliveryInformation(
                deliveryTime = OffsetDateTime.MIN,
                deliveryAddress = "서울시 강남구 테헤란로 1",
            ),
            orderLineItems = OrderLineItems(mutableListOf(
                OrderLineItem(quantity = 2, menuItemId = "americano", name = "Americano", price = Money("2500")),
            )),
        )
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(
        value = [
            NoopDuplicateMessageDetector::class,
            TramMessagingCommonAutoConfiguration::class,
            TramEventsCommonAutoConfiguration::class,
            TramInMemoryCommonConfiguration::class,
        ]
    )
    class TestConfiguration {
        @Bean
        fun orderController(orderService: OrderService) = OrderController(orderService)
    }
}
