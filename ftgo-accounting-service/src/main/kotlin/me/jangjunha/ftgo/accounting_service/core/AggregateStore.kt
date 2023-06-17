package me.jangjunha.ftgo.accounting_service.core

import com.eventstore.dbclient.AppendToStreamOptions
import com.eventstore.dbclient.EventStoreDBClient
import com.eventstore.dbclient.ExpectedRevision
import com.eventstore.dbclient.ReadStreamOptions

open class AggregateStore<A : Aggregate<ID, E>, ID, E : Any>(
    private val client: EventStoreDBClient,
    private val mapToStreamId: (ID) -> String,
    private val getEmpty: (ID) -> A,
) {
    fun append(
        id: ID,
        events: List<EventEnvelope<E>>,
        expectedRevision: ExpectedRevision = ExpectedRevision.any()
    ): ExpectedRevision {
        val streamId = mapToStreamId(id)
        val serializedEvents = events.map { it.serialize() }
        val result = client.appendToStream(
            streamId,
            AppendToStreamOptions.get().expectedRevision(expectedRevision),
            serializedEvents.iterator(),
        ).get()
        return result.nextExpectedRevision
    }

    fun get(id: ID): A {
        val streamId = mapToStreamId(id)
        val result = client.readStream(streamId, ReadStreamOptions.get().fromStart()).get()
        return result.events
            .map { EventEnvelope.deserialize<E>(it) }
            .fold(getEmpty(id)) { aggregate, event ->
                aggregate.apply(event.data)
                aggregate
            }
    }
}
