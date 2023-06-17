package me.jangjunha.ftgo.accounting_service.domain

import me.jangjunha.ftgo.accounting_service.api.events.AccountEvent
import me.jangjunha.ftgo.accounting_service.api.events.AccountOpened
import me.jangjunha.ftgo.accounting_service.core.Aggregate
import me.jangjunha.ftgo.accounting_service.core.EventEnvelope
import me.jangjunha.ftgo.common.Money
import java.util.UUID

data class Account(
    override val id: UUID,
    var balance: Money,
): Aggregate<UUID, AccountEvent> {

    fun open(): List<EventEnvelope<AccountEvent>> {
        return listOf(EventEnvelope(AccountOpened))
    }

    override fun apply(event: AccountEvent) {
        when(event) {
            is AccountOpened -> {
                balance = Money.ZERO
            }
        }
    }

    companion object {
        fun empty(id: UUID): Account {
            return Account(id, Money.ZERO)
        }
    }
}
