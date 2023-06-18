package me.jangjunha.ftgo.accounting_service

import io.eventuate.tram.commands.consumer.CommandHandlers
import io.eventuate.tram.commands.consumer.CommandHandlersBuilder
import io.eventuate.tram.commands.consumer.CommandMessage
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

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
            eventId = parseMessageIdAsUUID(cm.messageId),
            replyingHeaders = cm.correlationHeaders
        )
    }

    fun handleWithdraw(cm: CommandMessage<WithdrawCommand>) {
        cm.messageId
        accountingService.withdrawAccount(
            cm.command.accountId,
            cm.command.amount,
            cm.command.description,
            eventId = parseMessageIdAsUUID(cm.messageId),
            replyingHeaders = cm.correlationHeaders
        )
    }

    companion object {
        fun parseMessageIdAsUUID(messageId: String): UUID {
            val parts = messageId.split('-').map { it.toLong(16) }
            if (parts.size != 2) {
                throw IllegalStateException("Unexpected messageId format %s".format(messageId))
            }
            return UUID(
                parts[0],
                parts[1],
            )
        }
    }
}
