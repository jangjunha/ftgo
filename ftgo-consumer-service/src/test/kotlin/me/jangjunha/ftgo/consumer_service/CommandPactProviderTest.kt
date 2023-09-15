package me.jangjunha.ftgo.consumer_service

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
import me.jangjunha.ftgo.consumer_service.domain.ConsumerService
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
@Provider("ftgo-consumer-service")
@PactFilter(value = ["RawSynchronousMessage"], filter = ByInteractionType::class)
@PactBroker
class CommandPactProviderTest {

    @MockkBean
    lateinit var consumerService: ConsumerService

    @SpykBean
    lateinit var replyProducer: CommandReplyProducer

    @Autowired
    lateinit var commandDispatcher: CommandDispatcher

    @PactVerifyProvider("validated")
    fun validateValidate(messages: V4Interaction.SynchronousMessages): MessageAndMetadata {
        every {
            consumerService.validateOrderForConsumer(eq(UUID.fromString("627a9a8a-41af-4daf-a968-00ffc80b53ad")), any())
        }.returns(Unit)

        val commandMessage = buildMessage(messages.request)
        commandDispatcher.messageHandler(commandMessage)

        val slot = slot<List<Message>>()
        verify { replyProducer.sendReplies(any(), capture(slot)) }
        val reply = slot.captured[0]
        return MessageAndMetadata(reply.payload.encodeToByteArray(), reply.headers)
    }

    @State("the consumer")
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
        ConsumerServiceMessagingConfiguration::class,
    )
    class TestConfiguration
}
