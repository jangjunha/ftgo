package me.jangjunha.ftgo.accounting_service.domain

import com.eventstore.dbclient.EventStoreDBClient
import me.jangjunha.ftgo.accounting_service.api.events.AccountEvent
import me.jangjunha.ftgo.accounting_service.core.AggregateStore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountAggregateStore
@Autowired constructor(
    client: EventStoreDBClient,
) : AggregateStore<Account, UUID, AccountEvent>(
    client,
    { id -> "Account-%s".format(id.toString()) },
    Account::empty
) {
    companion object {
        fun idFromStreamId(streamId: String): UUID {
            return UUID.fromString(
                streamId.split('-', limit = 2).get(1)
            )
        }
    }
}
