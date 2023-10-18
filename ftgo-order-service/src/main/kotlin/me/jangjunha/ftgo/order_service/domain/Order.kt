package me.jangjunha.ftgo.order_service.domain

import com.google.protobuf.Timestamp
import io.eventuate.tram.events.aggregates.ResultWithDomainEvents
import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.common.UnsupportedStateTransitionException
import me.jangjunha.ftgo.common.protobuf.TimestampUtils
import me.jangjunha.ftgo.order_service.api.*
import me.jangjunha.ftgo.order_service.api.events.*
import java.util.*

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
data class Order(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID(0, 0),

    @Version
    val version: Long = 1,

    @Enumerated(EnumType.STRING)
    val state: OrderState = OrderState.APPROVAL_PENDING,

    val consumerId: UUID,
    val restaurantId: UUID,

    @Embedded
    val orderLineItems: OrderLineItems,

    @Embedded
    val deliveryInformation: DeliveryInformation,

    @Embedded
    val paymentInformation: PaymentInformation? = null,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(
            name = "amount",
            column = Column(name = "order_minimum"),
        ),
    )
    val orderMinimum: Money = Money(Integer.MAX_VALUE)
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
                        orderLineItems.map {
                            OrderDetails.LineItem(
                                it.quantity,
                                it.menuItemId,
                                it.name,
                                it.price,
                            )
                        },
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

    fun noteApproved(): ResultWithDomainEvents<Order, OrderDomainEvent> =
        when (state) {
            OrderState.APPROVAL_PENDING -> {
                ResultWithDomainEvents(
                    this.copy(state = OrderState.APPROVED),
                    OrderAuthorized()
                )
            }

            OrderState.APPROVED -> ResultWithDomainEvents(this, emptyList())

            else -> {
                throw UnsupportedStateTransitionException(state)
            }
        }

    fun noteRejected(): ResultWithDomainEvents<Order, OrderDomainEvent> {
        when (state) {
            OrderState.APPROVAL_PENDING -> {
                return ResultWithDomainEvents(
                    this.copy(state = OrderState.REJECTED),
                    OrderRejected(),
                )
            }

            else -> {
                throw UnsupportedStateTransitionException(state)
            }
        }
    }

    fun revise(orderRevision: OrderRevision): ResultWithDomainEvents<Pair<Order, LineItemQuantityChange>, OrderDomainEvent> {
        when (state) {
            OrderState.APPROVED -> {
                val change = orderLineItems.lineItemQuantityChange(orderRevision)
                if (!change.newOrderTotal.isGreaterThanOrEqual(orderMinimum)) {
                    throw OrderMinimumNotMetException()
                }
                return ResultWithDomainEvents(
                    Pair(
                        this.copy(state = OrderState.REVISION_PENDING),
                        change,
                    ),
                    OrderRevisionProposed(orderRevision, change.currentOrderTotal, change.newOrderTotal),
                )
            }

            else -> throw UnsupportedStateTransitionException(state)
        }
    }

    fun toAPI(): me.jangjunha.ftgo.order_service.api.Order = let { o ->
        order {
            id = o.id.toString()
            state = o.state
            consumerId = o.consumerId.toString()
            restaurantId = o.restaurantId.toString()
            lineItems.addAll(o.orderLineItems.lineItems.map { it.export() })
            deliveryInformation = o.deliveryInformation.let { d ->
                deliveryInformation {
                    deliveryTime = d.deliveryTime.run(TimestampUtils::toTimestamp)
                    deliveryAddress = d.deliveryAddress
                }
            }
            o.paymentInformation?.also { p ->
                paymentInformation = paymentInformation {
                    paymentToken = p.paymentToken
                }
            }
            orderMinimum = o.orderMinimum.toAPI()
        }
    }
}
