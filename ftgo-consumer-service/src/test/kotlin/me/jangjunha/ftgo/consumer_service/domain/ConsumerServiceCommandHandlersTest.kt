package me.jangjunha.ftgo.consumer_service.domain

import io.eventuate.tram.commands.common.Success
import io.eventuate.tram.testing.commands.CommandMessageHandlerUnitTestSupport.assertReplyTypeEquals
import io.mockk.mockk
import io.eventuate.tram.testing.commands.CommandMessageHandlerUnitTestSupport.given
import io.mockk.verify
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.consumer_service.api.command.ValidateOrderByConsumer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import java.util.*

class ConsumerServiceCommandHandlersTest {

    private lateinit var consumerService: ConsumerService
    private lateinit var consumerCommandHandlers: ConsumerServiceCommandHandlers

    companion object {
        val CONSUMER_ID = UUID.fromString("5799a153-35ef-42dd-b413-34e39c950d28")!!
        val ORDER_ID = UUID.fromString("3257f261-6f39-478e-b5d1-f108da615186")!!
    }

    @BeforeEach
    fun setUp() {
        consumerService = mockk(relaxed = true)
        consumerCommandHandlers = ConsumerServiceCommandHandlers(consumerService)
    }

    @Test
    fun validateOrderByConsumer() {
        given()
            .commandHandlers(consumerCommandHandlers.commandHandlers())
        .`when`()
            .receives(ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, Money.ZERO))
        .then()
            .verify {
                verify {
                    consumerService.validateOrderForConsumer(CONSUMER_ID, Money.ZERO)
                }
                assertReplyTypeEquals(Success::class.java, it)
            }
    }
}
