package me.jangjunha.ftgo.order_service.domain

import jakarta.persistence.Access
import jakarta.persistence.AccessType
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import me.jangjunha.ftgo.common.Money
import me.jangjunha.ftgo.order_service.api.OrderState
import java.util.*

@Entity
@Table(name = "orders")
@Access(AccessType.FIELD)
data class Order (
    @Id
    var id: UUID,

    @Version
    var version: Long,

    var state: OrderState,

    var consumerId: UUID,
    var restaurantId: UUID,

    @Embedded
    var orderLineItems: OrderLineItems,

    @Embedded
    var deliveryInformation: DeliveryInformation,

    @Embedded
    var paymentInformation: PaymentInformation,

    @Embedded
    var orderMinimum: Money = Money(Integer.MAX_VALUE)
)
