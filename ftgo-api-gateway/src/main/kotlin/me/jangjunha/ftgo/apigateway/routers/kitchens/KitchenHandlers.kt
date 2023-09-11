package me.jangjunha.ftgo.apigateway.routers.kitchens

import kotlinx.coroutines.*
import me.jangjunha.ftgo.apigateway.entities.restaurants.RestaurantInfo
import me.jangjunha.ftgo.apigateway.proxies.KitchenService
import me.jangjunha.ftgo.apigateway.proxies.RestaurantService
import me.jangjunha.ftgo.common.protobuf.TimestampUtils.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import java.util.UUID

@Component
class KitchenHandlers
@Autowired constructor(
    private val kitchenService: KitchenService,
    private val restaurantService: RestaurantService,
) {

    suspend fun getTicket(request: ServerRequest): ServerResponse = coroutineScope {
        val id = UUID.fromString(request.pathVariable("ticketId"))
        val deferredTicket = async { kitchenService.findTicketById(id) }
        val ticket = deferredTicket.await()

        val restaurantId = UUID.fromString(ticket.restaurantId)
        val deferredRestaurant = async { restaurantService.findRestaurantById(restaurantId) }
        val restaurant = deferredRestaurant.await()

        val ticketDetails = TicketDetails(
            id = id,
            state = ticket.state,
            sequence = ticket.sequence,
            readyBy = if (ticket.hasReadyBy()) fromTimestamp(ticket.readyBy) else null,
            acceptTime = if (ticket.hasAcceptTime()) fromTimestamp(ticket.acceptTime) else null,
            preparingTime = if (ticket.hasPreparingTime()) fromTimestamp(ticket.preparingTime) else null,
            pickedUpTime = if (ticket.hasPickedUpTime()) fromTimestamp(ticket.pickedUpTime) else null,
            readyForPickupTime = if (ticket.hasReadyForPickupTime()) fromTimestamp(ticket.readyForPickupTime) else null,
            restaurant = RestaurantInfo.from(restaurant),
        )
        ServerResponse.ok().bodyValueAndAwait(ticketDetails)
    }

    suspend fun acceptTicket(request: ServerRequest): ServerResponse = coroutineScope {
        val id = UUID.fromString(request.pathVariable("ticketId"))
        val payload = request.awaitBody(AcceptTicketRequest::class)
        kitchenService.acceptTicket(id, payload.readyBy)
        ServerResponse.accepted().buildAndAwait()
    }
}
