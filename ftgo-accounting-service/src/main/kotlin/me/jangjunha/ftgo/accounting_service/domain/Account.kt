package me.jangjunha.ftgo.accounting_service.domain

import me.jangjunha.ftgo.accounting_service.api.events.AccountDeposited
import me.jangjunha.ftgo.accounting_service.api.events.AccountEvent
import me.jangjunha.ftgo.accounting_service.api.events.AccountOpened
import me.jangjunha.ftgo.accounting_service.api.events.AccountWithdrawn
import me.jangjunha.ftgo.accounting_service.core.Aggregate
import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class Account(
    override val id: UUID,
    var balance: Money,
): Aggregate<UUID, AccountEvent> {

    fun open(): List<AccountEvent> {
        return listOf(AccountOpened)
    }

    fun deposit(amount: Money): List<AccountEvent> {
        return listOf(AccountDeposited(amount))
    }

    fun withdraw(amount: Money): List<AccountEvent> {
        if (!balance.isGreaterThanOrEqual(amount)) {
            throw RuntimeException("Cannot withdraw more than balance")
        }
        return listOf(AccountWithdrawn(amount))
    }

    override fun apply(event: AccountEvent) {
        when(event) {
            is AccountOpened -> {
                balance = Money.ZERO
            }
            is AccountDeposited -> {
                balance = balance.add(event.amount)
            }
            is AccountWithdrawn -> {
                balance = balance.add(event.amount.multiply(-1))
            }
        }
    }

    companion object {
        fun empty(id: UUID): Account {
            return Account(id, Money.ZERO)
        }
    }
}
