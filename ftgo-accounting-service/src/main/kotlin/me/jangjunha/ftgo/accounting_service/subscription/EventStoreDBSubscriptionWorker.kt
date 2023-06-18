package me.jangjunha.ftgo.accounting_service.subscription

import com.eventstore.dbclient.*
import me.jangjunha.ftgo.accounting_service.api.events.AccountEvent
import me.jangjunha.ftgo.accounting_service.core.EventSerializer
import me.jangjunha.ftgo.accounting_service.core.EventTypeMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.SmartLifecycle
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Component
class EventStoreDBSubscriptionWorker
@Autowired constructor(
    private val client: EventStoreDBClient,
    private val checkpointRepository: EventStoreDBSubscriptionCheckpointRepository,
    private val publisher: ApplicationEventPublisher,
) : SmartLifecycle, SubscriptionListener() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val subscriptionId = "default"
    private val subscriptionOptions = SubscribeToAllOptions.get()
        .fromStart()
        .filter(
            SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression("^[^\\$].*")
                .build()
        )
    private var subscription: Subscription? = null

    private val retryTemplate = RetryTemplate.builder()
        .infiniteRetry()
        .exponentialBackoff(100, 2.0, 5000)
        .build()

    override fun onEvent(subscription: Subscription, re: ResolvedEvent) {
        if (isEventWithEmptyData(re) || isCheckpointEvent(re)) return

        val streamEvent = try {
            EventSerializer.deserialize<AccountEvent>(re)
        } catch (e: Throwable) {
            logger.warn("Couldn't deserialize event with id: %s".format(re.event.eventId))
            return
        }

        publisher.publishEvent(streamEvent)
        checkpointRepository.store(subscriptionId, re.event.position.commitUnsigned)
    }

    override fun onError(subscription: Subscription, throwable: Throwable) {
        logger.error("Subscription was dropped", throwable)
        throw RuntimeException(throwable)
    }

    override fun start() {
        try {
            retryTemplate.execute<Unit, Throwable> {
                val checkpoint = checkpointRepository.load(subscriptionId)

                logger.info("Subscribing to all '%s'".format(subscriptionId))
                subscription = client.subscribeToAll(
                    this,
                    if (checkpoint != null) {
                        subscriptionOptions.fromPosition(Position(checkpoint, checkpoint))
                    } else {
                        subscriptionOptions.fromStart()
                    }
                ).get()
            }
        } catch (e: Throwable) {
            logger.error("Error while starting subscription", e)
            throw RuntimeException(e)
        }
    }

    override fun stop() {
        subscription?.stop()
        subscription = null
    }

    override fun isRunning(): Boolean {
        return subscription != null
    }

    private fun isEventWithEmptyData(resolvedEvent: ResolvedEvent): Boolean {
        if (resolvedEvent.event.eventData.isNotEmpty()) return false
        logger.info("Event without data received")
        return true
    }

    private fun isCheckpointEvent(resolvedEvent: ResolvedEvent): Boolean {
        if (resolvedEvent.event.eventType != EventTypeMapper.toName(CheckpointStored::class.java)) return false
        logger.info("Checkpoint event - ignoring")
        return true
    }
}
