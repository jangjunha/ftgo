package me.jangjunha.ftgo.accounting_service.core

import com.eventstore.dbclient.EventData
import com.eventstore.dbclient.EventDataBuilder
import com.eventstore.dbclient.ResolvedEvent
import com.fasterxml.jackson.databind.json.JsonMapper
import java.util.UUID

object EventSerializer {
    private val mapper = JsonMapper()

    fun serialize(data: Any, id: UUID? = null): EventData? {
        return EventDataBuilder
            .json(id, EventTypeMapper.toName(data::class.java), mapper.writeValueAsBytes(data))
            .build()
    }

    fun <E : Any> deserialize(re: ResolvedEvent): EventEnvelope<E> {
        val dataCls: Class<E> = EventTypeMapper.toClass(re.event.eventType) as Class<E>
        val data = mapper.readValue(re.event.eventData, dataCls)
        val metadata = EventMetadata(
            re.event.streamId,
            re.event.eventId,
            re.event.revision,
            re.event.position.commitUnsigned,
            re.event.eventType,
        )
        return EventEnvelope(data, metadata)
    }
}