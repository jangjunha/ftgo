package me.jangjunha.ftgo.order_history_service.messaging

import io.eventuate.tram.events.subscriber.DomainEventHandlers
import io.eventuate.tram.events.subscriber.DomainEventHandlersBuilder
import me.jangjunha.ftgo.order_history_service.dynamodb.SourceEvent
import me.jangjunha.ftgo.order_service.api.OrderServiceChannels
import org.slf4j.LoggerFactory

class OrderHistoryEventHandlers() {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun domainEventHandlers(): DomainEventHandlers {
        return DomainEventHandlersBuilder
            .forAggregateType(OrderServiceChannels.ORDER_EVENT_CHANNEL)
            .build()
    }
}
