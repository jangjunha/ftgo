package me.jangjunha.ftgo.consumer_service.domain

import io.eventuate.tram.events.publisher.ResultWithEvents
import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.consumer_service.api.event.ConsumerCreated
import java.util.UUID

@Entity
@Table(name = "consumers")
@Access(AccessType.FIELD)
data class Consumer(
    @Id
    val id: UUID = UUID(0, 0),
    val name: String,
) {
    fun validateOrderByConsumer(orderTotal: Money) {
        // TODO:
    }

    companion object {
        fun create(name: String, id: UUID? = null): ResultWithEvents<Consumer> {
            val consumer = Consumer(
                id = id ?: UUID.randomUUID(),
                name = name,
            )
            return ResultWithEvents(consumer, ConsumerCreated(consumer.id))
        }
    }
}
