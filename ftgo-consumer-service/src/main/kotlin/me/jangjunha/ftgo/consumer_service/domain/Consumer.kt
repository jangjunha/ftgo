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
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID(0, 0),
    val name: String,
) {
    fun validateOrderByConsumer(orderTotal: Money) {
        // TODO:
    }

    companion object {
        fun create(name: String): ResultWithEvents<Consumer> {
            val consumer = Consumer(name = name)
            return ResultWithEvents(consumer, ConsumerCreated())
        }
    }
}
