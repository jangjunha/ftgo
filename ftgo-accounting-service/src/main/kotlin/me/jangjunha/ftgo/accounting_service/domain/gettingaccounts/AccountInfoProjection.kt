package me.jangjunha.ftgo.accounting_service.domain.gettingaccounts

import jakarta.transaction.Transactional
import me.jangjunha.ftgo.accounting_service.api.events.AccountDeposited
import me.jangjunha.ftgo.accounting_service.api.events.AccountOpened
import me.jangjunha.ftgo.accounting_service.api.events.AccountWithdrawn
import me.jangjunha.ftgo.accounting_service.core.EventEnvelope
import me.jangjunha.ftgo.accounting_service.domain.AccountAggregateStore
import me.jangjunha.ftgo.common.Money
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.EventListener
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountInfoProjection
@Autowired constructor(
    private val repository: AccountInfoRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener
    @Transactional
    fun handleAccountOpened(envelope: EventEnvelope<AccountOpened>) {
        val id = mapId(envelope)
        if (repository.existsById(id)) {
            logger.warn("View with id %s was already applied for event %s".format(
                id,
                envelope.metadata.eventType
            ))
            return
        }
        repository.save(AccountInfo(
            id,
            Money.ZERO,
            0,
            Money.ZERO,
            0,
            lastProcessedPosition = envelope.metadata.logPosition,
        ))
    }

    @EventListener
    @Transactional
    fun handleAccountDeposited(envelope: EventEnvelope<AccountDeposited>) {
        val id = mapId(envelope)
        val event = envelope.data
        val info = repository.findByIdOrNull(id)
            ?: throw IllegalStateException(
                "Cannot find account info %s for deposited event %s".format(
                    id.toString(),
                    envelope.metadata.eventId.toString(),
                )
            )
        if (wasAlreadyApplied(info, envelope)) {
            logger.warn("View with id %s was already applied for event %s".format(
                id,
                envelope.metadata.eventType
            ))
            return
        }

        repository.save(info.copy(
            depositCount = info.depositCount + 1,
            depositAccumulate = info.depositAccumulate.add(event.amount),
            lastProcessedPosition = envelope.metadata.logPosition,
        ))
    }

    @EventListener
    @Transactional
    fun handleAccountWithdrawn(envelope: EventEnvelope<AccountWithdrawn>) {
        val id = mapId(envelope)
        val event = envelope.data
        val info = repository.findByIdOrNull(id)
            ?: throw IllegalStateException(
                "Cannot find account info %s for withdrawn event %s".format(
                    id.toString(),
                    envelope.metadata.eventId.toString(),
                )
            )
        if (wasAlreadyApplied(info, envelope)) {
            logger.warn("View with id %s was already applied for event %s".format(
                id,
                envelope.metadata.eventType
            ))
            return
        }

        repository.save(info.copy(
            withdrawCount = info.withdrawCount + 1,
            withdrawAccumulate = info.withdrawAccumulate.add(event.amount),
            lastProcessedPosition = envelope.metadata.logPosition,
        ))
    }

    private fun mapId(envelope: EventEnvelope<*>): UUID {
        return AccountAggregateStore.idFromStreamId(envelope.metadata.streamId)
    }

    companion object {
        private fun wasAlreadyApplied(entity: AccountInfo, envelope: EventEnvelope<*>): Boolean {
            return entity.lastProcessedPosition >= envelope.metadata.logPosition
        }
    }
}
