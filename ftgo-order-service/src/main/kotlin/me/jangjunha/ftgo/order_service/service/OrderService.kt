package me.jangjunha.ftgo.order_service.service

import io.eventuate.tram.events.aggregates.ResultWithDomainEvents
import io.eventuate.tram.sagas.orchestration.SagaInstanceFactory
import jakarta.transaction.Transactional
import me.jangjunha.ftgo.order_service.api.OrderDetails
import me.jangjunha.ftgo.order_service.api.events.OrderDomainEvent
import me.jangjunha.ftgo.order_service.domain.*
import me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSaga
import me.jangjunha.ftgo.order_service.sagas.createorder.CreateOrderSagaState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.UUID

@Service
class OrderService @Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val orderEventPublisher: OrderDomainEventPublisher,
    private val sagaInstanceFactory: SagaInstanceFactory,
    private val createOrderSaga: CreateOrderSaga,
) {
    fun createMenu(restaurantId: UUID, restaurantName: String, menuItems: List<MenuItem>) {
        val restaurant = Restaurant(restaurantId, menuItems.toMutableList(), restaurantName)
        restaurantRepository.save(restaurant)
    }

    fun reviseMenu(restaurantId: UUID, menuItems: List<MenuItem>) {
        val restaurant = restaurantRepository.findById(restaurantId).orElseThrow(::RuntimeException)
        restaurant.menuItems = menuItems.toMutableList()
        restaurantRepository.save(restaurant)
    }

    private fun makeOrderLineItems(
        items: List<MenuItemIdAndQuantity>,
        restaurant: Restaurant,
    ): List<OrderLineItem> {
        return items.map {
            val menuItem = (
                restaurant.menuItems.find { mi -> mi.id == it.menuItemId }
                    ?: throw InvalidMenuItemIdException(it.menuItemId)
            )
            OrderLineItem(it.quantity, it.menuItemId, menuItem.name, menuItem.price)
        }
    }

    fun getOrder(id: UUID): Order {
        return orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException(id)
    }

    @Transactional
    fun createOrder(
        consumerId: UUID,
        restaurantId: UUID,
        lineItems: List<MenuItemIdAndQuantity>,
        deliveryInformation: DeliveryInformation,
    ): Order {
        val restaurant = (
            restaurantRepository.findByIdOrNull(restaurantId)
                ?: throw RestaurantNotFoundException(restaurantId)
        )
        val orderLineItems = makeOrderLineItems(lineItems, restaurant)

        val oe = Order.createOrder(
            consumerId,
            restaurant,
            orderLineItems,
            deliveryInformation,
        )
        val order = orderRepository.save(oe.result)
        orderEventPublisher.publish(order, oe.events)

        val sagaState = CreateOrderSagaState(
            order.id,
            OrderDetails(
                orderLineItems.map { OrderDetails.LineItem(
                    it.quantity,
                    it.menuItemId,
                    it.name,
                    it.price,
                ) },
                order.orderLineItems.orderTotal,
                restaurantId,
                consumerId,
            ),
        )
        sagaInstanceFactory.create(createOrderSaga, sagaState)

        return order
    }

    @Transactional
    private fun updateOrder(
        id: UUID,
        updater: (Order) -> ResultWithDomainEvents<Order, OrderDomainEvent>,
    ): Order {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException(id)
        val rwe = updater(order)
        orderRepository.save(rwe.result)
        orderEventPublisher.publish(rwe.result, rwe.events)
        return order
    }

    fun approveOrder(id: UUID) {
        updateOrder(id, Order::noteApproved)
    }

    fun rejectOrder(id: UUID) {
        updateOrder(id, Order::noteRejected)
    }
}
