package me.jangjunha.ftgo.consumer_service.api.command

import io.eventuate.tram.commands.common.Command
import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class ValidateOrderByConsumer(
    val consumerId: UUID,
    val orderId: UUID,
    val orderTotal: Money,
): Command