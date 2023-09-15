package me.jangjunha.ftgo.accounting_service

import io.eventuate.tram.commands.consumer.CommandHandlers
import io.eventuate.tram.commands.consumer.CommandHandlersBuilder
import io.eventuate.tram.commands.consumer.CommandMessage
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import me.jangjunha.ftgo.accounting_service.domain.AccountLimitExceededException
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

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
        try {
            accountingService.withdrawAccount(
                cm.command.accountId,
                cm.command.amount,
                cm.command.description,
                eventId = parseMessageIdAsUUID(cm.messageId),
                replyingHeaders = cm.correlationHeaders
            )
        } catch (_: AccountLimitExceededException) {
        }
    }

    companion object {
        fun parseMessageIdAsUUID(messageId: String): UUID {
            return UUID.fromString(
                messageId
                    .replace("-", "")
                    .replaceFirst(
                        Regex("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)"),
                        "$1-$2-$3-$4-$5"
                    )
            )
        }
    }
}
