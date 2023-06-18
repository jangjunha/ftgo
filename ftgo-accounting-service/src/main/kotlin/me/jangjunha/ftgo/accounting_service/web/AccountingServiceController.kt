package me.jangjunha.ftgo.accounting_service.web

import me.jangjunha.ftgo.accounting_service.AccountingService
import me.jangjunha.ftgo.accounting_service.domain.Account
import me.jangjunha.ftgo.accounting_service.domain.gettingbyid.AccountDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping(path = ["/accounts/"])
class AccountingServiceController
@Autowired constructor(
    val accountingService: AccountingService,
) {

    @RequestMapping(method = [RequestMethod.POST])
    fun createAccount(): Account {
        return accountingService.createAccount()
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/{accountId}/"])
    fun getAccount(@PathVariable accountId: UUID): ResponseEntity<AccountDetails> {
        val accountDetails = accountingService.getAccount(accountId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        return ResponseEntity.ofNullable(accountDetails)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/{accountId}/deposit/"])
    fun depositAccount(@PathVariable accountId: UUID, @RequestBody requestBody: DepositRequest): Account {
        return accountingService.depositAccount(accountId, requestBody.amount)
    }

    @RequestMapping(method = [RequestMethod.POST], path = ["/{accountId}/withdraw/"])
    fun withdrawAccount(@PathVariable accountId: UUID, @RequestBody requestBody: WithdrawRequest): Account {
        return accountingService.withdrawAccount(accountId, requestBody.amount)
    }
}
