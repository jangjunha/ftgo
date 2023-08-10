package me.jangjunha.ftgo.order_service.grpc

import io.grpc.Status
import me.jangjunha.ftgo.order_service.api.*
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.domain.*
import me.jangjunha.ftgo.order_service.domain.DeliveryInformation
import me.jangjunha.ftgo.order_service.domain.MenuItemIdAndQuantity
import me.jangjunha.ftgo.order_service.service.OrderService
import java.time.OffsetDateTime
import java.util.*

class OrderServiceImpl(
    private val orderService: OrderService,
): OrderServiceGrpcKt.OrderServiceCoroutineImplBase() {

    override suspend fun getOrder(request: GetOrderPayload): Order {
        val order = try {
            orderService.getOrder(UUID.fromString(request.id))
        } catch (e: OrderNotFoundException) {
            throw Status.NOT_FOUND.withCause(e).asRuntimeException()
        }
        return order.toAPI()
    }

    override suspend fun createOrder(request: CreateOrderPayload): Order {
        val order = try {
            orderService.createOrder(
                UUID.fromString(request.consumerId),
                UUID.fromString(request.restaurantId),
                request.itemsList.map { MenuItemIdAndQuantity(it.menuItemId, it.quantity) },
                DeliveryInformation(
                    deliveryTime = OffsetDateTime.now().plusMinutes(60),
                    deliveryAddress = request.deliveryAddress,
                ),
            )
        } catch (e: RestaurantNotFoundException) {
            throw Status.INVALID_ARGUMENT.withCause(e).withDescription("invalid restaurant id").asRuntimeException()
        } catch (e: InvalidMenuItemIdException) {
            throw Status.INVALID_ARGUMENT.withCause(e).withDescription("invalid menuItemId ${e.menuItemId}").asRuntimeException()
        }
        return order.toAPI()
    }
}
