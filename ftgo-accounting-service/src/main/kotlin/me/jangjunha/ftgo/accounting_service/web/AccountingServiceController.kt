package me.jangjunha.ftgo.accounting_service.web

import me.jangjunha.ftgo.accounting_service.AccountingService
import me.jangjunha.ftgo.accounting_service.domain.Account
import me.jangjunha.ftgo.accounting_service.domain.gettingaccounts.AccountInfo
import me.jangjunha.ftgo.accounting_service.domain.gettingbyid.AccountDetails
import me.jangjunha.ftgo.common.auth.AuthenticatedClient
import me.jangjunha.ftgo.common.auth.AuthenticatedConsumerID
import me.jangjunha.ftgo.common.auth.AuthenticatedID
import me.jangjunha.ftgo.common.auth.AuthenticatedRestaurantID
import me.jangjunha.ftgo.common.web.AuthContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping(path = ["/accounts/"])
class AccountingServiceController
@Autowired constructor(
    val accountingService: AccountingService,
) {

    @RequestMapping(method = [RequestMethod.POST])
    fun createAccount(@AuthContext authenticatedID: AuthenticatedID?): Account {
        if (!hasPermission(null, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        return accountingService.createAccount()
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/{accountId}/"])
    fun getAccount(
        @AuthContext authenticatedID: AuthenticatedID?,
        @PathVariable accountId: UUID,
    ): ResponseEntity<AccountDetails> {
        if (!hasPermission(accountId, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val accountDetails = accountingService.getAccount(accountId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity.ofNullable(accountDetails)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/{accountId}/deposit/"])
    fun depositAccount(
        @AuthContext authenticatedID: AuthenticatedID?,
        @PathVariable accountId: UUID,
        @RequestBody requestBody: DepositRequest
    ): Account {
        if (!hasPermission(accountId, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        return accountingService.depositAccount(accountId, requestBody.amount)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/{accountId}/withdraw/"])
    fun withdrawAccount(
        @AuthContext authenticatedID: AuthenticatedID?,
        @PathVariable accountId: UUID,
        @RequestBody requestBody: WithdrawRequest
    ): Account {
        if (!hasPermission(accountId, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        return accountingService.withdrawAccount(accountId, requestBody.amount)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/account-infos/"])
    fun listAccount(
        @AuthContext authenticatedID: AuthenticatedID?,
        @RequestParam(defaultValue = "0") pageNumber: Int,
        @RequestParam(defaultValue = "10") pageSize: Int,
    ): List<AccountInfo> {
        if (!hasPermission(null, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        return accountingService.listAccount(pageNumber, pageSize)
    }

    private fun hasPermission(accountId: UUID?, authenticatedID: AuthenticatedID?): Boolean =
        when (authenticatedID) {
            null -> false
            is AuthenticatedClient -> true
            is AuthenticatedConsumerID -> accountId == authenticatedID.consumerId
            is AuthenticatedRestaurantID -> false
        }
}
