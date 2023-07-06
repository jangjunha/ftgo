package me.jangjunha.ftgo.order_history_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventEnvelope
import io.eventuate.tram.events.subscriber.DomainEventHandlers
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder
import me.jangjunha.ftgo.order_history_service.OrderHistoryDAO
import me.jangjunha.ftgo.order_history_service.domain.Order
import me.jangjunha.ftgo.order_history_service.domain.OrderLineItem
import me.jangjunha.ftgo.order_history_service.dynamodb.SourceEvent
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.api.events.OrderAuthorized
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime
import java.util.*

class OrderHistoryEventHandlers(
    private val orderHistoryDAO: OrderHistoryDAO,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun domainEventHandlers(): DomainEventHandlers {
        return DomainEventHandlersBuilder
            .forAggregateType(OrderServiceChannels.ORDER_EVENT_CHANNEL)
            .onEvent(OrderCreated::class.java, this::handleOrderCreated)
            .build()
    }

    private fun handleOrderCreated(env: DomainEventEnvelope<OrderCreated>) {
        logger.debug("handleOrderCreated called {}", env)
        val orderId = UUID.fromString(env.aggregateId)
        val order = Order(
            orderId,
            env.event.orderDetails.consumerId,
            OffsetDateTime.now(),
            OrderState.APPROVAL_PENDING,
            env.event.orderDetails.lineItems.map(OrderLineItem::from),
            env.event.orderDetails.restaurantId,
            env.event.restaurantName,
        )
        val result = orderHistoryDAO.addOrder(
            order,
            makeSourceEvent(env),
        )
        logger.debug("handleOrderCreated result {} {}", env, result)
    }

    companion object {
        private fun makeSourceEvent(env: DomainEventEnvelope<*>): SourceEvent {
            return SourceEvent(env.aggregateType, env.aggregateId, env.eventId)
        }
    }
}
