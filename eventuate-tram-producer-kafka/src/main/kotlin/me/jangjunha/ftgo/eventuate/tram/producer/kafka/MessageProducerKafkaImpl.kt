package me.jangjunha.ftgo.eventuate.tram.producer.kafka

import io.eventuate.common.id.ApplicationIdGenerator
import io.eventuate.common.id.IdGenerator
import io.eventuate.common.json.mapper.JSonMapper
import io.eventuate.messaging.kafka.producer.EventuateKafkaProducer
import io.eventuate.tram.messaging.common.Message
import io.eventuate.tram.messaging.producer.common.MessageProducerImplementation

class MessageProducerKafkaImpl(
    private val kafkaProducer: EventuateKafkaProducer,
): MessageProducerImplementation {
    private val idGenerator: IdGenerator = ApplicationIdGenerator()

    override fun send(message: Message?) {
        if (message == null) {
            return
        }

        val id = idGenerator.genId().asString()
        message.setHeader("ID", id)

        val destination = message.getRequiredHeader(Message.DESTINATION)
        val partitionId = message.getRequiredHeader(Message.PARTITION_ID)

        val key = partitionId ?: id
        val partition = kafkaProducer.partitionFor(
            destination,
            key,
        )

        val body = JSonMapper.toJson(ProducingMessage(message.headers, message.payload))
        kafkaProducer.send(destination, partition, key, body)
    }

    data class ProducingMessage(
        val headers: Any,
        val payload: Any,
    )
}
