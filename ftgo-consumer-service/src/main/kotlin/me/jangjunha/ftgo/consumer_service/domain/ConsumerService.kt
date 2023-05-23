package me.jangjunha.ftgo.consumer_service.domain

import io.eventuate.tram.events.publisher.DomainEventPublisher
import jakarta.transaction.Transactional
import me.jangjunha.ftgo.common.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConsumerService @Autowired constructor(
    val consumerRepository: ConsumerRepository,
    val domainEventPublisher: DomainEventPublisher,
) {
    @Transactional
    fun create(name: String): Consumer {
        val cwe = Consumer.create(name)
        val consumer = consumerRepository.save(cwe.result)
        domainEventPublisher.publish(Consumer::class.java, consumer.id, cwe.events)
        return consumer
    }

    fun findById(id: UUID): Consumer? {
        return consumerRepository.findByIdOrNull(id)
    }
}