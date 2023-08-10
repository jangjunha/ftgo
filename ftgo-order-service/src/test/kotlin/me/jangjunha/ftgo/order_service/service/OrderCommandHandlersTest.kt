package me.jangjunha.ftgo.order_service.service

import io.eventuate.tram.testing.commands.CommandMessageHandlerUnitTestSupport.given
import io.mockk.mockk
import io.mockk.verify
import me.jangjunha.ftgo.order_service.OrderFixtures.ORDER_ID
import me.jangjunha.ftgo.order_service.sagaparticipants.ApproveOrderCommand
import me.jangjunha.ftgo.order_service.sagaparticipants.RejectOrderCommand
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class OrderCommandHandlersTest {

    private lateinit var orderService: OrderService
    private lateinit var orderCommandHandlers: OrderCommandHandlers

    @BeforeEach
    fun setUp() {
        orderService = mockk(relaxed = true)
        orderCommandHandlers = OrderCommandHandlers(orderService)
    }

    @Test
    fun approveOrder() {
        given()
            .commandHandlers(orderCommandHandlers.commandHandlers())
        .`when`()
            .receives(ApproveOrderCommand(ORDER_ID))
        .then()
            .verify {
                verify {
                    orderService.approveOrder(ORDER_ID)
                }
            }
    }

    @Test
    fun rejectOrder() {
        given()
            .commandHandlers(orderCommandHandlers.commandHandlers())
        .`when`()
            .receives(RejectOrderCommand(ORDER_ID))
        .then()
            .verify {
                verify {
                    orderService.rejectOrder(ORDER_ID)
                }
            }
    }
}
