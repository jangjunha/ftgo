package me.jangjunha.ftgo.order_history_service

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.annotations.Pact
import au.com.dius.pact.core.model.messaging.Message
import au.com.dius.pact.core.model.messaging.MessagePact
import com.ninjasquad.springmockk.MockkBean
import io.eventuate.tram.events.subscriber.DomainEventDispatcher
import io.eventuate.tram.messaging.common.MessageImpl
import io.eventuate.tram.spring.inmemory.TramInMemoryCommonConfiguration
import io.mockk.*
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_history_service.domain.Order
import me.jangjunha.ftgo.order_history_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_history_service.dynamodb.SourceEvent
import me.jangjunha.ftgo.order_history_service.messaging.OrderHistoryServiceMessagingConfiguration
import me.jangjunha.ftgo.order_service.api.OrderState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.OffsetDateTime
import java.util.*


@ExtendWith(SpringExtension::class)
@ExtendWith(PactConsumerTestExt::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PactTestFor(providerName = "ftgo-order-service", providerType = ProviderType.ASYNCH, pactVersion = PactSpecVersion.V3)
class OrderServicePactTest {

    @MockkBean
    lateinit var orderHistoryDAO: OrderHistoryDAO

    @Autowired
    lateinit var domainEventDispatcher: DomainEventDispatcher

    @Pact(consumer = "ftgo-order-history-service")
    fun orderCreatedEvent(builder: MessagePactBuilder): MessagePact = builder
        .expectsToReceive("`OrderCreated` event")
        .withMetadata(
            mapOf(
                Pair("event-aggregate-type", "me.jangjunha.ftgo.order_service.domain.Order"),
                Pair("event-type", "me.jangjunha.ftgo.order_service.api.events.OrderCreated"),
                Pair("event-aggregate-id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                Pair("ID", ""),
            )
        )
        .withContent(
            PactDslJsonBody()
                .`object`(
                    "orderDetails", PactDslJsonBody()
                        .`object`("orderTotal", PactDslJsonBody().numberType("amount", 5000))
                        .uuid("restaurantId", "97e3c4c2-f336-4435-9314-ad1a633495df")
                        .uuid("consumerId", "627a9a8a-41af-4daf-a968-00ffc80b53ad")
                        .eachLike("lineItems")
                        .numberType("quantity", 2)
                        .stringType("menuItemId", "americano")
                        .stringType("name", "Americano")
                        .`object`("price", PactDslJsonBody().numberType("amount", 2500))
                        .closeObject()!!
                        .closeArray()!!
                )
                .stringType("deliveryAddress", "서울시 강남구 테헤란로 1")
                .stringType("restaurantName", "A Cafe")
        )
        .toPact()

    @Test
    @PactTestFor(pactMethod = "orderCreatedEvent")
    fun testConsumeOrderCreatedEvent(messages: List<Message>) {
        every { orderHistoryDAO.addOrder(any(), any()) } returns true

        val rawMessage = messages[0]
        val message = MessageImpl(rawMessage.contentsAsString(), rawMessage.metadata.mapValues { it.value.toString() })
        mockkStatic(OffsetDateTime::class) {
            every { OffsetDateTime.now() } returns OffsetDateTime.MIN
            domainEventDispatcher.messageHandler(message)
        }

        verify { orderHistoryDAO.addOrder(
            Order(
                orderId = UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                consumerId = UUID.fromString("627a9a8a-41af-4daf-a968-00ffc80b53ad"),
                creationDate = OffsetDateTime.MIN,
                status = OrderState.APPROVAL_PENDING,
                lineItems = listOf(
                    OrderLineItem(
                        quantity = 2,
                        menuItemId = "americano",
                        name = "Americano",
                        price = Money("2500"),
                    ),
                ),
                restaurantId = UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
                restaurantName = "A Cafe",
            ),
            SourceEvent(
                "me.jangjunha.ftgo.order_service.domain.Order",
                "6f2d06a3-5dd2-4096-8644-6084d64eae35",
                ""
            )
        ) }
    }

    @Pact(consumer = "ftgo-order-history-service")
    fun orderAuthorizedEvent(builder: MessagePactBuilder): MessagePact = builder
        .expectsToReceive("`OrderAuthorized` event")
        .withMetadata(
            mapOf(
                Pair("event-aggregate-type", "me.jangjunha.ftgo.order_service.domain.Order"),
                Pair("event-type", "me.jangjunha.ftgo.order_service.api.events.OrderAuthorized"),
                Pair("event-aggregate-id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                Pair("ID", ""),
            )
        )
        .withContent(PactDslJsonBody())
        .toPact()

    @Test
    @PactTestFor(pactMethod = "orderAuthorizedEvent")
    fun testConsumeOrderAuthorizedEvent(messages: List<Message>) {
        every { orderHistoryDAO.updateOrderState(any(), any(), any()) } returns true

        val rawMessage = messages[0]
        val message = MessageImpl(rawMessage.contentsAsString(), rawMessage.metadata.mapValues { it.value.toString() })
        domainEventDispatcher.messageHandler(message)

        verify { orderHistoryDAO.updateOrderState(
            id = UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
            state = OrderState.APPROVED,
            eventSource = SourceEvent(
                "me.jangjunha.ftgo.order_service.domain.Order",
                "6f2d06a3-5dd2-4096-8644-6084d64eae35",
                ""
            ),
        ) }
    }

    @Pact(consumer = "ftgo-order-history-service")
    fun orderRejectedEvent(builder: MessagePactBuilder): MessagePact = builder
        .expectsToReceive("`OrderRejected` event")
        .withMetadata(
            mapOf(
                Pair("event-aggregate-type", "me.jangjunha.ftgo.order_service.domain.Order"),
                Pair("event-type", "me.jangjunha.ftgo.order_service.api.events.OrderRejected"),
                Pair("event-aggregate-id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                Pair("ID", ""),
            )
        )
        .withContent(PactDslJsonBody())
        .toPact()

    @Test
    @PactTestFor(pactMethod = "orderRejectedEvent")
    fun testConsumeOrderRejectedEvent(messages: List<Message>) {
        every { orderHistoryDAO.updateOrderState(any(), any(), any()) } returns true

        val rawMessage = messages[0]
        val message = MessageImpl(rawMessage.contentsAsString(), rawMessage.metadata.mapValues { it.value.toString() })
        domainEventDispatcher.messageHandler(message)

        verify { orderHistoryDAO.updateOrderState(
            id = UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
            state = OrderState.REJECTED,
            eventSource = SourceEvent(
                "me.jangjunha.ftgo.order_service.domain.Order",
                "6f2d06a3-5dd2-4096-8644-6084d64eae35",
                ""
            ),
        ) }
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(
        value = [
            OrderHistoryServiceMessagingConfiguration::class,
            TramInMemoryCommonConfiguration::class,
        ]
    )
    class TestConfiguration
}
