package me.jangjunha.ftgo.consumer_service.web

import me.jangjunha.ftgo.consumer_service.domain.ConsumerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping(path = ["/consumers/"])
class ConsumerController @Autowired constructor(
    val consumerService: ConsumerService,
) {
    @RequestMapping(method = [RequestMethod.POST])
    fun create(@RequestBody requestBody: CreateConsumerRequest): CreateConsumerResponse {
        val consumer = consumerService.create(requestBody.name)
        return CreateConsumerResponse(consumer.id, consumer.name)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/{consumerId}/"])
    fun get(@PathVariable consumerId: UUID): ResponseEntity<GetConsumerResponse> {
        val consumer = (
            consumerService.findById(consumerId)
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        )
        return ResponseEntity(GetConsumerResponse(consumer.id, consumer.name), HttpStatus.OK)
    }
}