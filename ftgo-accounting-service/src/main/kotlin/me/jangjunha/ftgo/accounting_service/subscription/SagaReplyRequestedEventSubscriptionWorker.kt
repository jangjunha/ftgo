package me.jangjunha.ftgo.accounting_service.subscription

import com.eventstore.dbclient.*
import io.eventuate.tram.commands.common.CommandMessageHeaders
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder
import io.eventuate.tram.messaging.producer.MessageBuilder
import io.eventuate.tram.messaging.producer.MessageProducer
import me.jangjunha.ftgo.accounting_service.api.events.SagaReplyRequested
import me.jangjunha.ftgo.accounting_service.core.EventSerializer
import me.jangjunha.ftgo.accounting_service.core.EventTypeMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.SmartLifecycle
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Component
class SagaReplyRequestedEventSubscriptionWorker
@Autowired constructor(
    private val client: EventStoreDBClient,
    private val messageProducer: MessageProducer,
    private val checkpointRepository: EventStoreDBSubscriptionCheckpointRepository,
) : SmartLifecycle, SubscriptionListener() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val subscriptionId = "saga_replying"
    private val subscriptionOptions = SubscribeToAllOptions.get()
        .fromStart()
        .filter(
            SubscriptionFilter.newBuilder()
                .withEventTypeRegularExpression(
                    Regex.fromLiteral(
                        EventTypeMapper.toName(SagaReplyRequested::class.java)
                    ).toString()
                )
                .build()
        )
    private var subscription: Subscription? = null

    private val retryTemplate = RetryTemplate.builder()
        .infiniteRetry()
        .exponentialBackoff(100, 2.0, 5000)
        .build()

    override fun onEvent(subscription: Subscription, re: ResolvedEvent) {
        val envelope = try {
            EventSerializer.deserialize<SagaReplyRequested>(re)
        } catch (e: Throwable) {
            val msg = "Couldn't deserialize event with id: %s".format(re.event.eventId)
            logger.error(msg)
            throw IllegalStateException(msg)
        }
        val event = envelope.data

        val reply = CommandHandlerReplyBuilder.withSuccess()
        val destination = event.correlationHeaders[
            CommandMessageHeaders.inReply(CommandMessageHeaders.REPLY_TO)
        ]
        messageProducer.send(
            destination,
            MessageBuilder
                .withMessage(reply)
                .withExtraHeaders("", event.correlationHeaders)
                .build(),
        )

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

                logger.info("Subscribing to SagaReplyRequested '%s'".format(subscriptionId))
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
}
