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
import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.eventuate.tram.testutil.MessageReceiver
import me.jangjunha.ftgo.eventuate.tram.testutil.SagaMessagingTestHelper
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
    providerName = "ftgo-accounting-service",
    providerType = ProviderType.SYNCH_MESSAGE,
    pactVersion = PactSpecVersion.V4
)
class AccountingServiceProxyPactTest {

    @MockkBean
    lateinit var messageReceiver: MessageReceiver

    @Autowired
    lateinit var sagaMessagingTestHelper: SagaMessagingTestHelper

    @Pact(consumer = "ftgo-order-service")
    fun deposit(builder: SynchronousMessagePactBuilder): V4Pact = builder
        .given("the account")
        .expectsToReceive("deposited result")
        .withRequest { request ->
            request
                .withMetadata { metadata ->
                    metadata
                        .add("command_type", "me.jangjunha.ftgo.accounting_service.api.DepositCommand")
                        .add("command_saga_type", "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga")
                        .add("command_saga_id", Matchers.uuid())
                        .add(
                            "command_reply_to",
                            "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga-Reply"
                        )
                        .add("ID", "")
                }
                .withContent(
                    PactDslJsonBody()
                        .uuid("accountId", "627a9a8a-41af-4daf-a968-00ffc80b53ad")
                        .`object`("amount", PactDslJsonBody().numberType("amount", 5000))
                        .stringType("description", "")
                )
        }
        .withResponse { response ->
            response.withMetadata { metadata -> metadata.add("reply_outcome-type", "SUCCESS") }
        }
        .toPact()

    @Test
    @PactTestFor(pactMethod = "deposit")
    fun testDeposit(message: V4Interaction.SynchronousMessages) {
        val command = DepositCommand(UUID.fromString("627a9a8a-41af-4daf-a968-00ffc80b53ad"), Money("5000"), "")
        val sagaType = CreateOrderSaga::class.java.name
        every { messageReceiver.receive(eq("$sagaType-reply")) }.returns(buildMessage(message.response[0]))

        sagaMessagingTestHelper.sendCommand(
            AccountingServiceProxy.deposit,
            command,
            sagaType
        )
    }

    @Pact(consumer = "ftgo-order-service")
    fun withdraw(builder: SynchronousMessagePactBuilder): V4Pact = builder
        .given("the account")
        .expectsToReceive("withdrawn result")
        .withRequest { request ->
            request
                .withMetadata { metadata ->
                    metadata
                        .add("command_type", "me.jangjunha.ftgo.accounting_service.api.WithdrawCommand")
                        .add("command_saga_type", "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga")
                        .add("command_saga_id", Matchers.uuid())
                        .add(
                            "command_reply_to",
                            "me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga-Reply"
                        )
                        .add("ID", "")
                }
                .withContent(
                    PactDslJsonBody()
                        .uuid("accountId", "627a9a8a-41af-4daf-a968-00ffc80b53ad")
                        .`object`("amount", PactDslJsonBody().numberType("amount", 5000))
                        .stringType("description", "")
                )
        }
        .withResponse { response ->
            response.withMetadata { metadata -> metadata.add("reply_outcome-type", "SUCCESS") }
        }
        .toPact()

    @Test
    @PactTestFor(pactMethod = "withdraw")
    fun testWithdraw(message: V4Interaction.SynchronousMessages) {
        val command = WithdrawCommand(UUID.fromString("627a9a8a-41af-4daf-a968-00ffc80b53ad"), Money("5000"), "")
        val sagaType = CreateOrderSaga::class.java.name
        every { messageReceiver.receive(eq("$sagaType-reply")) }.returns(buildMessage(message.response[0]))

        sagaMessagingTestHelper.sendCommand(
            AccountingServiceProxy.withdraw,
            command,
            sagaType
        )
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
