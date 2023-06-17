package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.ExpectedRevision
import me.jangjunha.ftgo.accounting_service.domain.Account
import me.jangjunha.ftgo.accounting_service.domain.AccountAggregateStore
import me.jangjunha.ftgo.common.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountingService
@Autowired constructor(
    val accountAggregateStore: AccountAggregateStore,
) {

    fun createAccount(): Account {
        val account = Account.empty(UUID.randomUUID())

        val events = account.open()
        for (event in events) {
            account.apply(event.data)
        }
        accountAggregateStore.append(account.id, events, ExpectedRevision.noStream())

        return account
    }

    fun depositAccount(id: UUID, amount: Money): Account {
        val account = accountAggregateStore.get(id)

        val events = account.deposit(amount)
        for (event in events) {
            account.apply(event.data)
        }
        accountAggregateStore.append(account.id, events, ExpectedRevision.streamExists())

        return account
    }

    fun withdrawAccount(id: UUID, amount: Money): Account {
        val account = accountAggregateStore.get(id)

        val events = account.withdraw(amount)
        for (event in events) {
            account.apply(event.data)
        }
        accountAggregateStore.append(account.id, events, ExpectedRevision.streamExists())

        return account
    }

    fun getAccount(id: UUID): Account {
        return accountAggregateStore.get(id)
    }
}
