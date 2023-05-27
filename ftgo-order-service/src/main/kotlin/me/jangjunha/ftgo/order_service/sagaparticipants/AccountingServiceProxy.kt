package me.jangjunha.ftgo.order_service.sagaparticipants

import io.eventuate.tram.commands.common.Success
import io.eventuate.tram.sagas.simpledsl.CommandEndpointBuilder
import me.jangjunha.ftgo.accounting_service.api.AccountingServiceChannels
import me.jangjunha.ftgo.accounting_service.api.AuthorizeCommand
import org.springframework.stereotype.Component

@Component
object AccountingServiceProxy {
    val authorize = CommandEndpointBuilder
        .forCommand(AuthorizeCommand::class.java)
        .withChannel(AccountingServiceChannels.accountingServiceChannel)
        .withReply(Success::class.java)
        .build()
}
