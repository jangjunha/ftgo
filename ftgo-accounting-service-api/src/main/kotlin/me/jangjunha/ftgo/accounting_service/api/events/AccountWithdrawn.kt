package me.jangjunha.ftgo.accounting_service.api.events

import me.jangjunha.ftgo.common.Money

data class AccountWithdrawn(
    val amount: Money = Money.ZERO,
    val description: String? = null,
): AccountEvent()
