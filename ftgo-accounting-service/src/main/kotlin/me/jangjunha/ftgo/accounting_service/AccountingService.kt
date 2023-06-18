package me.jangjunha.ftgo.accounting_service

import com.eventstore.dbclient.ExpectedRevision
import me.jangjunha.ftgo.accounting_service.api.events.SagaReplyRequested
import me.jangjunha.ftgo.accounting_service.domain.Account
import me.jangjunha.ftgo.accounting_service.domain.AccountAggregateStore
import me.jangjunha.ftgo.accounting_service.domain.AccountLimitExceededException
import me.jangjunha.ftgo.accounting_service.domain.gettingaccounts.AccountInfo
import me.jangjunha.ftgo.accounting_service.domain.gettingaccounts.AccountInfoRepository
import me.jangjunha.ftgo.accounting_service.domain.gettingbyid.AccountDetails
import me.jangjunha.ftgo.accounting_service.domain.gettingbyid.AccountDetailsRepository
import me.jangjunha.ftgo.common.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

@Service
class AccountingService
@Autowired constructor(
    val accountAggregateStore: AccountAggregateStore,
    val accountDetailsRepository: AccountDetailsRepository,
    val accountInfoRepository: AccountInfoRepository,
) {

    fun createAccount(id: UUID? = null): Account {
        val account = Account.empty(id ?: UUID.randomUUID())

        val event = Pair(null, account.open())
        account.apply(event.second)
        accountAggregateStore.append(account.id, listOf(event), ExpectedRevision.noStream())

        return account
    }

    fun depositAccount(
        accountId: UUID,
        amount: Money,
        description: String? = null,
        eventId: UUID? = null,
        replyingHeaders: Map<String, String>? = null
    ): Account {
        val account = accountAggregateStore.get(accountId)

        val events = listOf(
            Pair(eventId, account.deposit(amount, description))
        ) + if (replyingHeaders != null) {
            listOf(Pair(null, SagaReplyRequested(replyingHeaders)))
        } else {
            emptyList()
        }

        for (event in events) {
            account.apply(event.second)
        }
        accountAggregateStore.append(account.id, events, ExpectedRevision.streamExists())

        return account
    }

    fun withdrawAccount(
        accountId: UUID,
        amount: Money,
        description: String? = null,
        eventId: UUID? = null,
        replyingHeaders: Map<String, String>? = null
    ): Account {
        val account = accountAggregateStore.get(accountId)

        val event = try {
            account.withdraw(amount, description)
        } catch (e: AccountLimitExceededException) {
            if (replyingHeaders != null) {
                accountAggregateStore.append(
                    account.id,
                    listOf(
                        Pair(
                            null,
                            SagaReplyRequested(replyingHeaders, status = SagaReplyRequested.SagaReplyStatus.FAILURE)
                        )
                    ),
                    ExpectedRevision.streamExists()
                )
            }
            throw e
        }

        account.apply(event)

        val events = listOfNotNull(
            Pair(eventId, event),
            if (replyingHeaders != null) {
                Pair(null, SagaReplyRequested(replyingHeaders))
            } else {
                null
            }
        )
        accountAggregateStore.append(account.id, events, ExpectedRevision.streamExists())

        return account
    }

    fun getAccount(id: UUID): AccountDetails? {
        return accountDetailsRepository.findByIdOrNull(id)
    }

    fun listAccount(page: Int, pageSize: Int): List<AccountInfo> {
        return accountInfoRepository.findAll(PageRequest.of(page, pageSize)).toList()
    }
}
