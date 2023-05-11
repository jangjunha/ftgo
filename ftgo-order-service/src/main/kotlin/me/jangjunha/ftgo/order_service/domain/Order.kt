package me.jangjunha.ftgo.order_service.domain

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents
import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.api.OrderLineItem
import me.jangjunha.ftgo.order_service.api.OrderState
import me.jangjunha.ftgo.order_service.api.events.OrderCreated
import me.jangjunha.ftgo.order_service.api.events.OrderDomainEvent
import java.util.*

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
data class Order (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID,

    @Version
    var version: Long = 1,

    var state: OrderState = OrderState.APPROVAL_PENDING,

    var consumerId: UUID,
    var restaurantId: UUID,

    @Embedded
    var orderLineItems: OrderLineItems,

    @Embedded
    var deliveryInformation: DeliveryInformation,

    @Embedded
    var paymentInformation: PaymentInformation? = null,

    @Embedded
    var orderMinimum: Money = Money(Integer.MAX_VALUE)
) {
    companion object {
        fun createOrder(
            consumerId: UUID,
            restaurant: Restaurant,
            orderLineItems: List<OrderLineItem>,
            deliveryInformation: DeliveryInformation,
        ): ResultWithDomainEvents<Order, OrderDomainEvent> {
            val order = Order(
                null!!,
                consumerId = consumerId,
                restaurantId = restaurant.id,
                orderLineItems = OrderLineItems(orderLineItems.toMutableList()),
                deliveryInformation = deliveryInformation,
            )
            val events = listOf(
                OrderCreated(
                    OrderDetails(
                        orderLineItems,
                        order.orderLineItems.orderTotal,
                        restaurant.id,
                        consumerId,
                    ),
                    deliveryInformation.deliveryAddress,
                    restaurant.name
                )
            )
            return ResultWithDomainEvents(order, events);
        }
    }
}
