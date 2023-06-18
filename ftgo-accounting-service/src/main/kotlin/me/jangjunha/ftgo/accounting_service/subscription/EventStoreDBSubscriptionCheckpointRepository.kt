package me.jangjunha.ftgo.accounting_service.subscription

import com.eventstore.dbclient.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.OffsetDateTime

@Component
class EventStoreDBSubscriptionCheckpointRepository
@Autowired constructor(
    private val client: EventStoreDBClient,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun load(subscriptionId: String): Long? {
        val streamName = getCheckpointStreamName(subscriptionId)
        val result = try {
            client.readStream(
                streamName,
                ReadStreamOptions.get().backwards().fromEnd()
            ).get()
        } catch (e: Throwable) {
            if (e.cause !is StreamNotFoundException) {
                logger.error("Failed to load checkpoint", e)
                throw RuntimeException(e)
            }
            return null
        }

        return result.events
            .map(CheckpointStored::deserialize)
            .map { it.position }
            .firstOrNull()
    }

    fun store(subscriptionId: String, position: Long) {
        val streamName = getCheckpointStreamName(subscriptionId)
        val event = CheckpointStored(subscriptionId, position, OffsetDateTime.now()).serialize()
        try {
            client.appendToStream(
                streamName,
                AppendToStreamOptions.get().expectedRevision(ExpectedRevision.streamExists()),
                event,
            ).get()
        } catch (e: Throwable) {
            if (e.cause !is WrongExpectedVersionException) {
                throw RuntimeException(e)
            }

            // WrongExpectedVersionException means that stream did not exist
            // Set the checkpoint stream to have at most 1 event
            // using stream metadata $maxCount property

            val keepOnlyLastEvent = StreamMetadata()
            keepOnlyLastEvent.maxCount = 1

            try {
                client.setStreamMetadata(
                    streamName,
                    AppendToStreamOptions.get().expectedRevision(ExpectedRevision.noStream()),
                    keepOnlyLastEvent
                ).get()

                // append event again expecting stream to not exist
                client.appendToStream(
                    streamName,
                    AppendToStreamOptions.get().expectedRevision(ExpectedRevision.noStream()),
                    event,
                ).get()
            } catch (exception: Exception) {
                throw RuntimeException(e)
            }
        }
    }

    private fun getCheckpointStreamName(subscriptionId: String): String {
        return "checkpoint_%s".format(subscriptionId)
    }
}
