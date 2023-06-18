package me.jangjunha.ftgo.accounting_service.domain

import me.jangjunha.ftgo.accounting_service.api.events.*
import me.jangjunha.ftgo.accounting_service.core.Aggregate
import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class Account(
    override val id: UUID,
    var balance: Money,
) : Aggregate<UUID, AccountEvent> {

    fun open(): AccountEvent {
        return AccountOpened
    }

    fun deposit(amount: Money, description: String? = null): AccountEvent {
        return AccountDeposited(amount, description)
    }

    fun withdraw(amount: Money, description: String? = null): AccountEvent {
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw AccountLimitExceededException(amount, balance)
        }
        return AccountWithdrawn(amount, description)
    }

    override fun apply(event: AccountEvent) {
        when (event) {
            is AccountOpened -> {
                balance = Money.ZERO
            }

            is AccountDeposited -> {
                balance = balance.add(event.amount)
            }

            is AccountWithdrawn -> {
                balance = balance.add(event.amount.multiply(-1))
            }

            is SagaReplyRequested -> {}
        }
    }

    companion object {
        fun empty(id: UUID): Account {
            return Account(id, Money.ZERO)
        }
    }
}
