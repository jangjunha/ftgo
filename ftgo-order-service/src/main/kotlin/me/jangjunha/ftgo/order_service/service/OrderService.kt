package me.jangjunha.ftgo.order_service.service

import jakarta.transaction.Transactional
import me.jangjunha.ftgo.order_service.domain.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.UUID
import java.util.stream.Stream

@Service
class OrderService @Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
    private val orderRepository: OrderRepository,
    private val orderEventPublisher: OrderDomainEventPublisher,
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

        return order
    }

    private fun updateOrder(
        id: UUID,
        updater: (Order) -> List<OrderDomainEvent>,
    ): Order {
        val order = orderRepository.findByIdOrNull(id) ?: throw OrderNotFoundException(id)
        val events = updater(order)
        orderEventPublisher.publish(order, events)
        return order
    }

    fun approveOrder(id: UUID) {
        updateOrder(id, Order::noteApproved)
    }

    fun rejectOrder(id: UUID) {
        updateOrder(id, Order::noteRejected)
    }
}