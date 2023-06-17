package me.jangjunha.ftgo.accounting_service.web

import me.jangjunha.ftgo.accounting_service.AccountingService
import me.jangjunha.ftgo.accounting_service.domain.Account
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
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
    fun getAccount(@PathVariable accountId: UUID): Account {
        return accountingService.getAccount(accountId)
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
