package me.jangjunha.ftgo.order_service

import au.com.dius.pact.consumer.dsl.Matchers
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.dsl.SynchronousMessagePactBuilder
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.core.model.PactSpecVersion
import au.com.dius.pact.core.model.V4Interaction
import au.com.dius.pact.core.model.V4Pact
import au.com.dius.pact.core.model.annotations.Pact
import io.eventuate.common.json.mapper.JSonMapper
import io.eventuate.tram.consumer.common.NoopDuplicateMessageDetector
import io.eventuate.tram.spring.events.common.TramEventsCommonAutoConfiguration
import io.eventuate.tram.spring.inmemory.TramInMemoryCommonConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.util.*

@ExtendWith(PactConsumerTestExt::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@PactTestFor(
    providerName = "ftgo-kitchen-service",
    providerType = ProviderType.SYNCH_MESSAGE,
    pactVersion = PactSpecVersion.V4
)
class KitchenPactTest {

    @Pact(consumer = "ftgo-order-service")
    fun createTicketCommand(builder: SynchronousMessagePactBuilder): V4Pact = builder
        .given("'A Cafe' restaurant")
        .expectsToReceive("created ticket")
        .withRequest { request ->
            request
                .withMetadata { metadata ->
                    metadata
                        .add("command_type", "me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket")
                        .add("command_saga_type", "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga")
                        .add("command_saga_id", Matchers.uuid("87a5f784-2254-4996-bc94-53474f4cdee8"))
                        .add(
                            "command_reply_to",
                            "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga-Reply"
                        )
                        .add("ID", "")
                }
                .withContent(
                    PactDslJsonBody()
                        .uuid("orderId", "6f2d06a3-5dd2-4096-8644-6084d64eae35")
                        .uuid("restaurantId", "97e3c4c2-f336-4435-9314-ad1a633495df")
                        .`object`(
                            "ticketDetails")
                                .eachLike("lineItems")
                                    .numberType("quantity", 2)
                                    .stringType("menuItemId", "americano")
                                    .stringType("name", "Americano")
                                    .closeObject()!!
                                    .closeArray()!!
                        .closeObject()!!
                )
        }
        .withResponse { response ->
            response
                .withMetadata { metadata ->
                    metadata
                        .add("reply_type", "me.jangjunha.ftgo.kitchen_service.api.CreateTicketReply")
                        .add("reply_outcome-type", "SUCCESS")
                }
                .withContent(
                    PactDslJsonBody()
                        .uuid("ticketId", "6f2d06a3-5dd2-4096-8644-6084d64eae35")
                        .numberType("sequence", 101L)
                )
        }
        .toPact()

    @Test
    @PactTestFor(pactMethod = "createTicketCommand")
    fun testCreateTicketCommand(message: V4Interaction.SynchronousMessages) {
//        assert(message.request.metadata == mutableMapOf(
//            Pair("command_type", "me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket"),
//            Pair("command_saga_type", "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga"),
//            Pair("command_saga_id", "87a5f784-2254-4996-bc94-53474f4cdee8"),
//            Pair("command_reply_to", "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga-Reply"),
//        ))
        println(message.request.contents.valueAsString())
        assert(JSonMapper.fromJson(message.request.contents.valueAsString(), CreateTicket::class.java) == CreateTicket(
            UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
            TicketDetails(listOf(
                TicketDetails.LineItem(2, "americano", "Americano"),
            )),
            UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df"),
        ))

        // TODO: test consume
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
