package me.jangjunha.ftgo.order_service

import au.com.dius.pact.core.model.Pact
import au.com.dius.pact.provider.MessageAndMetadata
import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit5.MessageTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactFilter
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import io.eventuate.common.json.mapper.JSonMapper
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.inmemory.TramInMemoryCommonConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.api.events.OrderAuthorized
import me.jangjunha.ftgo.order_service.api.events.OrderCreated
import me.jangjunha.ftgo.order_service.api.events.OrderRejected
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Provider("ftgo-order-service")
@PactFilter(value = ["Message"], filter = ByInteractionType::class)
@PactBroker
class OrderMessagingPactProviderTest {

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun testTemplate(pact: Pact, context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = MessageTestTarget()
    }

    @PactVerifyProvider("`OrderCreated` event")
    fun orderCreatedEvent(): MessageAndMetadata {
        val event = OrderCreated(
            OrderDetails(
                listOf(
                    OrderDetails.LineItem(2, "americano", "Americano", Money("2500")),
                ),
                Money("5000"),
                UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
                UUID.fromString("627a9a8a-41af-4daf-a968-00ffc80b53ad")
            ),
            "서울시 강남구 테헤란로 1",
            "A Cafe"
        )
        return MessageAndMetadata(
            JSonMapper.toJson(event).toByteArray(),
            mapOf(
                Pair("event-type", "me.jangjunha.ftgo.order_service.api.events.OrderCreated"),
                Pair("event-aggregate-type", "me.jangjunha.ftgo.order_service.domain.Order"),
                Pair("event-aggregate-id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                Pair("ID", ""),
            ),
        )
    }

    @PactVerifyProvider("`OrderAuthorized` event")
    fun orderAuthorizedEvent(): MessageAndMetadata {
        val event = OrderAuthorized()
        return MessageAndMetadata(
            JSonMapper.toJson(event).toByteArray(),
            mapOf(
                Pair("event-type", "me.jangjunha.ftgo.order_service.api.events.OrderAuthorized"),
                Pair("event-aggregate-type", "me.jangjunha.ftgo.order_service.domain.Order"),
                Pair("event-aggregate-id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                Pair("ID", ""),
            ),
        )
    }

    @PactVerifyProvider("`OrderRejected` event")
    fun orderRejectedEvent(): MessageAndMetadata {
        val event = OrderRejected()
        return MessageAndMetadata(
            JSonMapper.toJson(event).toByteArray(),
            mapOf(
                Pair("event-type", "me.jangjunha.ftgo.order_service.api.events.OrderRejected"),
                Pair("event-aggregate-type", "me.jangjunha.ftgo.order_service.domain.Order"),
                Pair("event-aggregate-id", "6f2d06a3-5dd2-4096-8644-6084d64eae35"),
                Pair("ID", ""),
            ),
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
    class TestConfiguration
}
