package me.jangjunha.ftgo.accounting_service.domain

import me.jangjunha.ftgo.common.Money

class AccountLimitExceededException(
    requestedAmount: Money,
    balance: Money,
) : RuntimeException("Requested amount %.2f is greater than balance %.2f".format(
    requestedAmount.amount,
    balance.amount,
))
