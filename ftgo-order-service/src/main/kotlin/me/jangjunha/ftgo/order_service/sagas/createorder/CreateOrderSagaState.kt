package me.jangjunha.ftgo.order_service.sagas.createorder

import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.consumer_service.api.command.ValidateOrderByConsumer
import me.jangjunha.ftgo.kitchen_service.api.CreateTicketReply
import me.jangjunha.ftgo.kitchen_service.api.TicketDetails
import me.jangjunha.ftgo.kitchen_service.api.TicketLineItem
import me.jangjunha.ftgo.kitchen_service.api.commands.CancelCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.ConfirmCreateTicket
import me.jangjunha.ftgo.kitchen_service.api.commands.CreateTicket
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.sagaparticipants.ApproveOrderCommand
import me.jangjunha.ftgo.order_service.sagaparticipants.RejectOrderCommand
import org.slf4j.LoggerFactory
import java.util.UUID

data class CreateOrderSagaState (
    val orderId: UUID,
    val orderDetails: OrderDetails,
    var ticketId: UUID? = null,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun makeRejectOrderCommand(): RejectOrderCommand {
        return RejectOrderCommand(orderId)
    }

    fun makeValidateOrderByConsumerCommand(): ValidateOrderByConsumer {
        logger.info("makeValidateOrderByConsumerCommand")
        return ValidateOrderByConsumer(
            orderDetails.consumerId,
            orderId,
            orderDetails.orderTotal,
        )
    }

    fun makeCreateTicketCommand(): CreateTicket {
        logger.info("makeCreateTicketCommand")
        val ticketDetails = TicketDetails(orderDetails.lineItems.map {
            TicketLineItem.newBuilder()
                .setQuantity(it.quantity)
                .setMenuItemId(it.menuItemId)
                .setName(it.name)
                .build()
        })
        return CreateTicket(orderId, ticketDetails, orderDetails.restaurantId)
    }

    fun handleCreateTicketReply(reply: CreateTicketReply) {
        logger.info("handleCreateTicketReply")
        logger.debug("getTicketId {}", reply.ticketId)
        ticketId = reply.ticketId
    }

    fun makeCancelCreateTicketCommand(): CancelCreateTicket {
        return CancelCreateTicket(ticketId)
    }

    fun makeWithdrawCommand(): WithdrawCommand {
        logger.info("makeWithdrawCommand")
        return WithdrawCommand(
            orderDetails.consumerId,
            orderDetails.orderTotal,
            "order:%s".format(orderId),
        )
    }

    fun makeDepositCommand(): DepositCommand {
        return DepositCommand(
            orderDetails.consumerId,
            orderDetails.orderTotal,
            "cancel order:%s".format(orderId),
        )
    }

    fun makeApproveOrderCommand(): ApproveOrderCommand {
        // TODO: 이거 안되는 것 같아서 확인해보기
        logger.info("makeApproveOrderCommand")
        return ApproveOrderCommand(orderId)
    }

    fun makeConfirmCreateTicketCommand(): ConfirmCreateTicket {
        return ConfirmCreateTicket(ticketId)
    }

    protected constructor(): this(UUID(0, 0), OrderDetails(listOf(), Money.ZERO, UUID(0, 0), UUID(0, 0)))
}
