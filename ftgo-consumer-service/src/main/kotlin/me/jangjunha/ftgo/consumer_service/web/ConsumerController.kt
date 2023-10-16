package me.jangjunha.ftgo.consumer_service.web

import me.jangjunha.ftgo.common.auth.AuthenticatedClient
import me.jangjunha.ftgo.common.auth.AuthenticatedConsumerID
import me.jangjunha.ftgo.common.auth.AuthenticatedCourierID
import me.jangjunha.ftgo.common.auth.AuthenticatedID
import me.jangjunha.ftgo.common.auth.AuthenticatedRestaurantID
import me.jangjunha.ftgo.common.web.AuthContext
import me.jangjunha.ftgo.consumer_service.domain.ConsumerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping(path = ["/consumers/"])
class ConsumerController @Autowired constructor(
    val consumerService: ConsumerService,
) {
    @RequestMapping(method = [RequestMethod.POST])
    fun create(
        @AuthContext authenticatedID: AuthenticatedID?,
        @RequestBody requestBody: CreateConsumerRequest
    ): CreateConsumerResponse {
        if (!hasPermission(null, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val consumer = consumerService.create(requestBody.name)
        return CreateConsumerResponse(consumer.id, consumer.name)
    }

    @RequestMapping(method = [RequestMethod.GET], path = ["/{consumerId}/"])
    fun get(
        @AuthContext authenticatedID: AuthenticatedID?,
        @PathVariable consumerId: UUID
    ): ResponseEntity<GetConsumerResponse> {
        if (!hasPermission(consumerId, authenticatedID)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN)
        }
        val consumer = (
                consumerService.findById(consumerId)
                    ?: return ResponseEntity(HttpStatus.NOT_FOUND)
                )
        return ResponseEntity(GetConsumerResponse(consumer.id, consumer.name), HttpStatus.OK)
    }

    private fun hasPermission(consumerId: UUID?, authenticatedID: AuthenticatedID?): Boolean =
        when (authenticatedID) {
            null -> false
            is AuthenticatedClient -> true
            is AuthenticatedConsumerID -> consumerId == authenticatedID.consumerId
            is AuthenticatedRestaurantID, is AuthenticatedCourierID -> false
        }
}