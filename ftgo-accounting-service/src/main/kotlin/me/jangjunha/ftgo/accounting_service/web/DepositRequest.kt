package me.jangjunha.ftgo.accounting_service.web

import me.jangjunha.ftgo.common.Money

data class DepositRequest(
    val amount: Money = Money.ZERO,
)
