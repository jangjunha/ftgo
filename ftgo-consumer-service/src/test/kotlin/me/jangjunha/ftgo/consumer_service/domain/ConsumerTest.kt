package me.jangjunha.ftgo.consumer_service.domain

import me.jangjunha.ftgo.consumer_service.api.event.ConsumerCreated
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class ConsumerTest {
    @Test
    fun createConsumer() {
        val id = UUID.fromString("bb66593b-f88b-488f-a0ae-e7038be1c284")
        val re = Consumer.create("yerin", id = id)
        assertEquals(Consumer(
            id = id,
            name = "yerin",
        ), re.result)
        assertEquals(listOf(
            ConsumerCreated(id),
        ), re.events)
    }
}
