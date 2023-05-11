package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.*
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
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
)
