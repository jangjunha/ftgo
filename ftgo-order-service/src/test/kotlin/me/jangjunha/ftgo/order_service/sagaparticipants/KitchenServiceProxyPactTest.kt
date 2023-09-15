package me.jangjunha.ftgo.order_service.sagaparticipants

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
import au.com.dius.pact.core.model.v4.MessageContents
import com.ninjasquad.springmockk.MockkBean
import io.eventuate.common.id.IdGenerator
import io.eventuate.tram.messaging.common.Message
import io.eventuate.tram.messaging.common.MessageImpl
import io.eventuate.tram.sagas.orchestration.SagaCommandProducer
import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration
import io.eventuate.tram.sagas.spring.orchestration.SagaOrchestratorConfiguration
import io.eventuate.tram.spring.commands.common.TramCommandsCommonAutoConfiguration
import io.eventuate.tram.spring.messaging.common.TramMessagingCommonAutoConfiguration
import io.mockk.every
import me.jangjunha.ftgo.eventuate.tram.testutil.MessageReceiver
import me.jangjunha.ftgo.eventuate.tram.testutil.SagaMessagingTestHelper
import me.jangjunha.ftgo.kitchen_service.api.CreateTicketReply
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket
import me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
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
class KitchenServiceProxyPactTest {

    @MockkBean
    lateinit var messageReceiver: MessageReceiver

    @Autowired
    lateinit var sagaMessagingTestHelper: SagaMessagingTestHelper


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
                            "ticketDetails"
                        )
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
        val command = CreateTicket(
            UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"),
            TicketDetails(
                listOf(
                    TicketDetails.LineItem(2, "americano", "Americano"),
                ),
            ),
            UUID.fromString("97e3c4c2-f336-4435-9314-ad1a633495df")
        )
        val expectedReply = CreateTicketReply(UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35"), 101L)
        val sagaType = CreateOrderSaga::class.java.name
        every { messageReceiver.receive(eq("$sagaType-reply")) }.returns(buildMessage(message.response[0]))

        val reply = sagaMessagingTestHelper.sendAndReceiveCommand(
            KitchenServiceProxy.create,
            command,
            CreateTicketReply::class.java,
            sagaType
        )
        assert(reply == expectedReply)
    }

    private fun buildMessage(contents: MessageContents): Message = MessageImpl(
        contents.contents.valueAsString(),
        contents.metadata.mapValues { it.value.toString() },
    )

    @Configuration
    @EnableAutoConfiguration
    @Import(
        value = [
            TramMessagingCommonAutoConfiguration::class,
            TramCommandsCommonAutoConfiguration::class,
            TramSagaInMemoryConfiguration::class,
            SagaOrchestratorConfiguration::class,
        ]
    )
    class TestConfiguration {

        @Bean
        fun sagaMessagingTestHelper(
            messageReceiver: MessageReceiver,
            sagaCommandProducer: SagaCommandProducer,
            idGenerator: IdGenerator
        ) = SagaMessagingTestHelper(messageReceiver, sagaCommandProducer, idGenerator)
    }
}
