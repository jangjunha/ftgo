package me.jangjunha.ftgo.accounting_service.subscription

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventDataBuilder
import com.eventstore.dbclient.ResolvedEvent
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.jangjunha.ftgo.accounting_service.core.EventTypeMapper
import java.time.OffsetDateTime
import java.util.*

data class CheckpointStored(
    val subscriptionId: String = "",
    val position: Long = 0,
    val checkpointedAt: OffsetDateTime = OffsetDateTime.MIN,
) {
    fun serialize(): EventData {
        return EventDataBuilder.json(
            UUID.randomUUID(),
            EventTypeMapper.toName(CheckpointStored::class.java),
            mapper.writeValueAsBytes(this)
        ).build()
    }

    companion object {
        private val mapper = JsonMapper()
            .registerModule(JavaTimeModule())

        fun deserialize(re: ResolvedEvent): CheckpointStored {
            return mapper.readValue(
                re.event.eventData,
                CheckpointStored::class.java,
            )
        }
    }
}
