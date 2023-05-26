package me.jangjunha.ftgo.order_service.domain

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents
import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.common.UnsupportedStateTransitionException
import me.jangjunha.ftgo.order_service.api.*
import me.jangjunha.ftgo.order_service.api.events.*
import java.util.*

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
data class Order (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID = UUID(0, 0),

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

    fun noteApproved(): List<OrderDomainEvent> {
        when (state) {
            OrderState.APPROVAL_PENDING -> {
                this.state = OrderState.APPROVED
                return listOf(OrderAuthorized())
            }
            else -> {
                throw UnsupportedStateTransitionException(state)
            }
        }
    }

    fun noteRejected(): List<OrderDomainEvent> {
        when (state) {
            OrderState.APPROVAL_PENDING -> {
                this.state = OrderState.REJECTED
                return listOf(OrderRejected())
            }
            else -> {
                throw UnsupportedStateTransitionException(state)
            }
        }
    }

    fun revise(orderRevision: OrderRevision): ResultWithDomainEvents<LineItemQuantityChange, OrderDomainEvent> {
        when (state) {
            OrderState.APPROVED -> {
                val change = orderLineItems.lineItemQuantityChange(orderRevision)
                if (!change.newOrderTotal.isGreaterThanOrEqual(orderMinimum)) {
                    throw OrderMinimumNotMetException()
                }
                this.state = OrderState.REVISION_PENDING
                return ResultWithDomainEvents(change, listOf(
                    OrderRevisionProposed(orderRevision, change.currentOrderTotal, change.newOrderTotal),
                ))
            }
            else -> throw UnsupportedStateTransitionException(state)
        }
    }
}
