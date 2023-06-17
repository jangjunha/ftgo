package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.ExpectedRevision
import me.jangjunha.ftgo.accounting_service.domain.Account
import me.jangjunha.ftgo.accounting_service.domain.AccountAggregateStore
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

    fun getAccount(id: UUID): Account {
        return accountAggregateStore.get(id)
    }
}
