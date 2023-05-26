package me.jangjunha.ftgo.order_service.service

import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder.withSuccess
import io.eventuate.tram.commands.consumer.CommandHandlers
import io.eventuate.tram.commands.consumer.CommandHandlersBuilder
import io.eventuate.tram.commands.consumer.CommandMessage
import io.eventuate.tram.messaging.common.Message
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import me.jangjunha.ftgo.order_service.sagaparticipants.ApproveOrderCommand
import me.jangjunha.ftgo.order_service.sagaparticipants.RejectOrderCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderCommandHandlers @Autowired constructor(
    private val orderService: OrderService,
) {
    fun commandHandlers(): CommandHandlers {
        return CommandHandlersBuilder
            .fromChannel(OrderServiceChannels.COMMAND_CHANNEL)
            .onMessage(ApproveOrderCommand::class.java, this::handleApproveOrder)
            .onMessage(RejectOrderCommand::class.java, this::handleRejectOrder)
            .build()
    }

    fun handleApproveOrder(cm: CommandMessage<ApproveOrderCommand>): Message {
        val orderId = cm.command.orderId
        orderService.approveOrder(orderId)
        return withSuccess()
    }

    fun handleRejectOrder(cm: CommandMessage<RejectOrderCommand>): Message {
        val orderId = cm.command.orderId
        orderService.rejectOrder(orderId)
        return withSuccess()
    }
}
