package me.jangjunha.ftgo.consumer_service.domain

import io.eventuate.tram.events.publisher.DomainEventPublisher
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.consumer_service.api.event.ConsumerCreated
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.data.repository.findByIdOrNull
import java.util.*

class ConsumerServiceTest {

    private lateinit var consumerRepository: ConsumerRepository
    private lateinit var domainEventPublisher: DomainEventPublisher
    private lateinit var consumerService: ConsumerService

    companion object {
        val INE_ID = UUID.fromString("5ba8369b-e58a-4aab-8933-7dcb6261fa83")
        val INE = Consumer(id = INE_ID, name = "INE")
    }

    @BeforeEach
    fun setUp() {
        consumerRepository = mockk()
        val consumerSlot = slot<Consumer>()
        every { consumerRepository.save(capture(consumerSlot)) } answers { consumerSlot.captured }
        every { consumerRepository.findByIdOrNull(any()) } answers { null }
        every { consumerRepository.findByIdOrNull(INE_ID) } answers { INE }

        domainEventPublisher = mockk(relaxed = true)
        consumerService = ConsumerService(consumerRepository, domainEventPublisher)
    }

    @Test
    fun create() {
        val consumer = consumerService.create("yerin")
        assertEquals("yerin", consumer.name)
        verify { consumerRepository.save(consumer) }
        verify {
            domainEventPublisher.publish(
                Consumer::class.java, consumer.id, listOf(
                    ConsumerCreated(consumer.id),
                )
            )
        }
    }

    @Test
    fun findById() {
        assertEquals(
            INE,
            consumerService.findById(INE_ID),
        )
        assertNull(consumerService.findById(UUID(1, 2)))
    }

    @Test
    fun validateOrderForConsumer() {
        // TODO:
        assertDoesNotThrow {
            consumerService.validateOrderForConsumer(INE_ID, Money.ZERO)
        }
        assertThrows(ConsumerNotFoundException::class.java) {
            consumerService.validateOrderForConsumer(UUID(1, 2), Money.ZERO)
        }
    }
}
