package me.jangjunha.ftgo.order_service.sagas.createorder

import org.junit.jupiter.api.Test
import io.eventuate.tram.sagas.testing.SagaUnitTestSupport.given;
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.consumer_service.api.ConsumerServiceChannels
import me.jangjunha.ftgo.consumer_service.api.command.ValidateOrderByConsumer
import me.jangjunha.ftgo.kitchen_service.api.KitchenServiceChannels
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails
import me.jangjunha.ftgo.kitchen_service.api.commands.CancelCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.ConfirmCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket
import me.jangjunha.ftgo.order_service.OrderFixtures.CONSUMER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.ORDER_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.RESTAURANT_ID
import me.jangjunha.ftgo.order_service.OrderFixtures.TWO_LATTE_ORDER_DETAILS
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import me.jangjunha.ftgo.order_service.sagaparticipants.*

class CreateOrderSagaTest {

    private val orderServiceProxy = OrderServiceProxy
    private val kitchenServiceProxy = KitchenServiceProxy
    private val consumerServiceProxy = ConsumerServiceProxy
    private val accountingServiceProxy = AccountingServiceProxy

    fun makeCreateOrderSaga(): CreateOrderSaga = CreateOrderSaga(
        accountingServiceProxy,
        consumerServiceProxy,
        kitchenServiceProxy,
        orderServiceProxy,
    )

    @Test
    fun shouldRejectOrderDueToConsumerVerificationFailed() {
        given()
            .saga(makeCreateOrderSaga(), CreateOrderSagaState(ORDER_ID, TWO_LATTE_ORDER_DETAILS))
        .expect()
            .command(ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, Money("2500")))
            .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
            .failureReply()
        .expect()
            .command(RejectOrderCommand(ORDER_ID))
            .to(OrderServiceChannels.COMMAND_CHANNEL)
    }

    @Test
    fun shouldRejectOrderDueToCreateTicketFailed() {
        given()
            .saga(
                makeCreateOrderSaga(),
                CreateOrderSagaState(ORDER_ID, TWO_LATTE_ORDER_DETAILS)
            )
        .expect()
            .command(ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, Money("2500")))
            .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(
                CreateTicket(
                    ORDER_ID,
                    TicketDetails(listOf(
                        TicketDetails.LineItem(2, "latte", "Cafe Latte")
                    )),
                    RESTAURANT_ID,
                ),
            )
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
        .andGiven()
            .failureReply()
        .expect()
            .command(RejectOrderCommand(ORDER_ID))
            .to(OrderServiceChannels.COMMAND_CHANNEL)
    }

    @Test
    fun shouldRejectOrderDueToInsufficientBalance() {
        given()
            .saga(
                makeCreateOrderSaga(),
                CreateOrderSagaState(ORDER_ID, TWO_LATTE_ORDER_DETAILS)
            )
        .expect()
            .command(ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, Money("2500")))
            .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(
                CreateTicket(
                    ORDER_ID,
                    TicketDetails(listOf(
                        TicketDetails.LineItem(2, "latte", "Cafe Latte")
                    )),
                    RESTAURANT_ID,
                ),
            )
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
        .andGiven()
            .successReply()
        .expect()
            .command(
                WithdrawCommand(
                    CONSUMER_ID,
                    Money("2500"),
                    "order:43517ead-0606-4d49-98f9-6b6b873b944e"
                ),
            )
            .to(AccountingServiceChannels.accountingServiceChannel)
        .andGiven()
            .failureReply()
        .expect()
            .command(CancelCreateTicket(ORDER_ID))
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
        .andGiven()
            .successReply()
        .expect()
            .command(RejectOrderCommand(ORDER_ID))
            .to(OrderServiceChannels.COMMAND_CHANNEL)
    }


    @Test
    fun shouldCreateOrder() {
        given()
            .saga(
                makeCreateOrderSaga(),
                CreateOrderSagaState(ORDER_ID, TWO_LATTE_ORDER_DETAILS)
            )
        .expect()
            .command(ValidateOrderByConsumer(CONSUMER_ID, ORDER_ID, Money("2500")))
            .to(ConsumerServiceChannels.consumerServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(
                CreateTicket(
                    ORDER_ID,
                    TicketDetails(listOf(
                        TicketDetails.LineItem(2, "latte", "Cafe Latte")
                    )),
                    RESTAURANT_ID,
                ),
            )
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
        .andGiven()
            .successReply()
        .expect()
            .command(
                WithdrawCommand(
                    CONSUMER_ID,
                    Money("2500"),
                    "order:43517ead-0606-4d49-98f9-6b6b873b944e"
                ),
            )
            .to(AccountingServiceChannels.accountingServiceChannel)
        .andGiven()
            .successReply()
        .expect()
            .command(ConfirmCreateTicket(ORDER_ID))
            .to(KitchenServiceChannels.COMMAND_CHANNEL)
        .andGiven()
            .successReply()
        .expect()
            .command(ApproveOrderCommand(ORDER_ID))
            .to(OrderServiceChannels.COMMAND_CHANNEL)
    }
}
