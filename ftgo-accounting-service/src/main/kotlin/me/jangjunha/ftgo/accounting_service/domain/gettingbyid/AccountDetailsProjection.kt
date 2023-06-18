package me.jangjunha.ftgo.accounting_service.domain.gettingbyid

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
class AccountDetailsProjection
@Autowired constructor(
    private val repository: AccountDetailsRepository,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @EventListener
    @Transactional
    fun handleAccountOpened(envelope: EventEnvelope<AccountOpened>) {
        val id = mapId(envelope)
        if (repository.existsById(id)) {
            logger.warn(
                "View with id %s was already applied for event %s".format(
                    id,
                    envelope.metadata.eventType
                )
            )
            return
        }
        val entity = AccountDetails(
            id,
            Money.ZERO,
            version = envelope.metadata.streamPosition,
            lastProcessedPosition = envelope.metadata.logPosition,
        )
        repository.save(entity)
    }

    @Transactional
    private fun handleUpdate(envelope: EventEnvelope<*>, updater: (AccountDetails) -> AccountDetails) {
        val id = mapId(envelope)

        val entity = repository.findByIdOrNull(id)
        if (entity == null) {
            logger.error(
                "View with id %s was not found for event %s".format(
                    id,
                    envelope.metadata.eventType
                )
            )
            return
        }
        if (wasAlreadyApplied(entity, envelope)) {
            logger.warn(
                "View with id %s was already applied for event %s".format(
                    id,
                    envelope.metadata.eventType
                )
            )
            return
        }

        val updated = updater(entity)
        repository.save(updated)
    }

    @EventListener
    fun handleAccountDeposited(envelope: EventEnvelope<AccountDeposited>) {
        handleUpdate(envelope) {
            it.copy(
                amount = it.amount.add(envelope.data.amount),
                version = envelope.metadata.streamPosition,
                lastProcessedPosition = envelope.metadata.logPosition,
            )
        }
    }

    @EventListener
    fun handleAccountWithdrawn(envelope: EventEnvelope<AccountWithdrawn>) {
        handleUpdate(envelope) {
            it.copy(
                amount = it.amount.add(envelope.data.amount.multiply(-1)),
                version = envelope.metadata.streamPosition,
                lastProcessedPosition = envelope.metadata.logPosition,
            )
        }
    }

    private fun mapId(envelope: EventEnvelope<*>): UUID {
        return AccountAggregateStore.idFromStreamId(envelope.metadata.streamId)
    }

    companion object {
        private fun wasAlreadyApplied(entity: AccountDetails, envelope: EventEnvelope<*>): Boolean {
            return entity.lastProcessedPosition >= envelope.metadata.logPosition
        }
    }
}
