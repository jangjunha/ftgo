package me.jangjunha.ftgo.order_service

import au.com.dius.pact.core.model.V4Interaction
import au.com.dius.pact.core.model.v4.MessageContents
import au.com.dius.pact.provider.MessageAndMetadata
import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit5.MessageTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junitsupport.Provider
import au.com.dius.pact.provider.junitsupport.State
import au.com.dius.pact.provider.junitsupport.loader.PactBroker
import au.com.dius.pact.provider.junitsupport.loader.PactFilter
import au.com.dius.pact.provider.spring.junit5.PactVerificationSpringProvider
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.eventuate.tram.commands.consumer.CommandDispatcher
import io.eventuate.tram.commands.consumer.CommandReplyProducer
import io.eventuate.tram.messaging.common.Message
import io.eventuate.tram.messaging.common.MessageImpl
import io.eventuate.tram.sagas.spring.inmemory.TramSagaInMemoryConfiguration
import io.mockk.every
import io.mockk.slot
import io.mockk.verify
import me.jangjunha.ftgo.order_service.service.OrderService
import me.jangjunha.ftgo.order_service.service.OrderServiceMessagingConfiguration
import me.jangjunha.ftgo.pact.provider.junitsupport.filter.ByInteractionType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*


@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Provider("ftgo-order-service")
@PactFilter(value = ["RawSynchronousMessage"], filter = ByInteractionType::class)
@PactBroker
class CommandPactProviderTest {

    @MockkBean
    lateinit var orderService: OrderService

    @SpykBean
    lateinit var replyProducer: CommandReplyProducer

    @Autowired
    lateinit var commandDispatcher: CommandDispatcher

    @PactVerifyProvider("rejected")
    fun validateReject(messages: V4Interaction.SynchronousMessages): MessageAndMetadata {
        every {
            orderService.rejectOrder(eq(UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35")))
        }.returns(Unit)

        val commandMessage = buildMessage(messages.request)
        commandDispatcher.messageHandler(commandMessage)

        val slot = slot<List<Message>>()
        verify { replyProducer.sendReplies(any(), capture(slot)) }
        val reply = slot.captured[0]
        return MessageAndMetadata(reply.payload.encodeToByteArray(), reply.headers)
    }

    @PactVerifyProvider("approved")
    fun validateApprove(messages: V4Interaction.SynchronousMessages): MessageAndMetadata {
        every {
            orderService.approveOrder(eq(UUID.fromString("6f2d06a3-5dd2-4096-8644-6084d64eae35")))
        }.returns(Unit)

        val commandMessage = buildMessage(messages.request)
        commandDispatcher.messageHandler(commandMessage)

        val slot = slot<List<Message>>()
        verify { replyProducer.sendReplies(any(), capture(slot)) }
        val reply = slot.captured[0]
        return MessageAndMetadata(reply.payload.encodeToByteArray(), reply.headers)
    }

    @State("order")
    fun toConsumerExists() {
    }

    @TestTemplate
    @ExtendWith(PactVerificationSpringProvider::class)
    fun testTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @BeforeEach
    fun setUp(context: PactVerificationContext) {
        context.target = MessageTestTarget()
    }

    private fun buildMessage(contents: MessageContents): Message {
        return MessageImpl(
            contents.contents.valueAsString(),
            contents.metadata.mapValues { it.value.toString() },
        )
    }

    @Configuration
    @EnableAutoConfiguration
    @Import(
        TramSagaInMemoryConfiguration::class,
        OrderServiceMessagingConfiguration::class,
    )
    class TestConfiguration
}
