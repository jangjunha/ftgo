package me.jangjunha.ftgo.order_service.service

import me.jangjunha.ftgo.order_service.domain.MenuItem
import me.jangjunha.ftgo.order_service.domain.Restaurant
import me.jangjunha.ftgo.order_service.domain.RestaurantRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.UUID
import java.util.stream.Stream

@Service
class OrderService @Autowired constructor(
    private val restaurantRepository: RestaurantRepository,
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
}
