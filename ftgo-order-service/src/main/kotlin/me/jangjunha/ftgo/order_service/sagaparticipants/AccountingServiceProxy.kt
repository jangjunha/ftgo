package me.jangjunha.ftgo.order_service.sagaparticipants

import io.eventuate.tram.commands.common.Success
import io.eventuate.tram.sagas.simpledsl.CommandEndpoint
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.DepositCommand
import me.jangjunha.ftgo.accounting_service.api.WithdrawCommand
import org.springframework.stereotype.Component

@Component
object AccountingServiceProxy {
    val deposit: CommandEndpoint<DepositCommand> = CommandEndpointBuilder
        .forCommand(DepositCommand::class.java)
        .withChannel(AccountingServiceChannels.accountingServiceChannel)
        .withReply(Success::class.java)
        .build()

    val withdraw: CommandEndpoint<WithdrawCommand> = CommandEndpointBuilder
        .forCommand(WithdrawCommand::class.java)
        .withChannel(AccountingServiceChannels.accountingServiceChannel)
        .withReply(Success::class.java)
        .build()
}
