package me.jangjunha.ftgo.accounting_service.api

import io.eventuate.tram.commands.common.Command
import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class AuthorizeCommand(
    val consumerId: UUID,
    val orderId: UUID,
    val orderTotal: Money,
): Command
