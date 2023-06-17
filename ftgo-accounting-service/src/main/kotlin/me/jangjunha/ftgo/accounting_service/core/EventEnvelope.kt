package me.jangjunha.ftgo.accounting_service.core

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventDataBuilder
import com.eventstore.dbclient.ResolvedEvent
import com.fasterxml.jackson.databind.json.JsonMapper

data class EventEnvelope<E : Any>(
    val data: E,
    val metadata: EventMetadata = EventMetadata(),
) {

    fun serialize(): EventData? {
        return EventDataBuilder
            .json(EventTypeMapper.toName(data::class.java), mapper.writeValueAsBytes(data))
            .metadataAsBytes(mapper.writeValueAsBytes(metadata))
            .build()
    }

    companion object {
        val mapper = JsonMapper()

        fun <E: Any> deserialize(event: ResolvedEvent): EventEnvelope<E> {
            val dataCls: Class<E> = EventTypeMapper.toClass(event.event.eventType) as Class<E>
            val data = mapper.readValue(event.event.eventData, dataCls)
            val metadata = mapper.readValue(event.event.userMetadata, EventMetadata::class.java)
            return EventEnvelope(data, metadata)
        }
    }
}
