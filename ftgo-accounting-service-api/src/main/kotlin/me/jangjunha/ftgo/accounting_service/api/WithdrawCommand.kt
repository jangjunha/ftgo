package me.jangjunha.ftgo.accounting_service.api

import io.eventuate.tram.commands.common.Command
import me.jangjunha.ftgo.common.Money
import java.util.*

data class WithdrawCommand(
    val accountId: UUID = UUID(0, 0),
    val amount: Money = Money.ZERO,
    val description: String = "",
): Command
