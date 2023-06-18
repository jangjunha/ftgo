package me.jangjunha.ftgo.accounting_service

import io.eventuate.tram.commands.consumer.CommandHandlers
import io.eventuate.tram.commands.consumer.CommandHandlersBuilder
import io.eventuate.tram.commands.consumer.CommandMessage
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class AccountingServiceCommandHandler
@Autowired constructor(
    private val accountingService: AccountingService,
) {
    fun commandHandlers(): CommandHandlers {
        return CommandHandlersBuilder
            .fromChannel(AccountingServiceChannels.accountingServiceChannel)
            .onMessage(DepositCommand::class.java, this::handleDeposit)
            .onMessage(WithdrawCommand::class.java, this::handleWithdraw)
            .build()
    }

    fun handleDeposit(cm: CommandMessage<DepositCommand>) {
        accountingService.depositAccount(
            cm.command.accountId,
            cm.command.amount,
            cm.command.description,
            replyingHeaders = cm.correlationHeaders
        )
    }

    fun handleWithdraw(cm: CommandMessage<WithdrawCommand>) {
        accountingService.withdrawAccount(
            cm.command.accountId,
            cm.command.amount,
            cm.command.description,
            replyingHeaders = cm.correlationHeaders
        )
    }
}
