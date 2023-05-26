package me.jangjunha.ftgo.order_service.domain

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface OrderRepository: CrudRepository<Order, UUID>
