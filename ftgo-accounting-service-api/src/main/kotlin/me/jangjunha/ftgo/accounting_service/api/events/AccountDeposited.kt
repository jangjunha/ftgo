package me.jangjunha.ftgo.accounting_service.api.events

import me.jangjunha.ftgo.common.Money

data class AccountDeposited(
    val amount: Money = Money.ZERO,
): AccountEvent()
