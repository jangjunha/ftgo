package me.jangjunha.ftgo.consumer_service.web

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import jakarta.persistence.EntityManager
import me.jangjunha.ftgo.consumer_service.domain.Consumer
import me.jangjunha.ftgo.consumer_service.domain.ConsumerService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.*

@WebMvcTest
class ConsumerControllerTest
@Autowired constructor(
    private val mockMvc: MockMvc,
) {
    @MockkBean
    lateinit var consumerService: ConsumerService

    @MockkBean
    lateinit var entityManager: EntityManager

    @Test
    fun create() {
        every { consumerService.create(any()) } returns Consumer(
            UUID.fromString("56ac7b67-b74a-459f-b055-7fe7e1dab3b8"),
            ""
        )

        mockMvc.perform(
            post("/consumers/")
                .accept(MediaType.APPLICATION_JSON)
                .header("x-ftgo-authenticated-client-id", "foo")
                .content("""{"name": "yerin"}""")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value("56ac7b67-b74a-459f-b055-7fe7e1dab3b8"))

        verify {
            consumerService.create("yerin")
        }
    }

    @Test
    fun get() {
        every {
            consumerService.findById(UUID.fromString("d8db6819-6ab1-4099-9db8-98c3e03cf848"))
        } returns Consumer(id = UUID.fromString("d8db6819-6ab1-4099-9db8-98c3e03cf848"), name = "jieun")

        mockMvc.perform(
            get("/consumers/d8db6819-6ab1-4099-9db8-98c3e03cf848/")
                .accept(MediaType.APPLICATION_JSON)
                .header("x-ftgo-authenticated-consumer-id", "d8db6819-6ab1-4099-9db8-98c3e03cf848")
        )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("\$.id").value("d8db6819-6ab1-4099-9db8-98c3e03cf848"))
            .andExpect(jsonPath("\$.name").value("jieun"))
    }
}