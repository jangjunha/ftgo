package me.jangjunha.ftgo.order_service.grpc

import io.grpc.Status
import io.grpc.stub.StreamObserver
import me.jangjunha.ftgo.order_service.api.CreateOrderPayload
import me.jangjunha.ftgo.order_service.api.GetOrderPayload
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpc;
import me.jangjunha.ftgo.order_service.domain.*
import me.jangjunha.ftgo.order_service.service.OrderService
import java.time.OffsetDateTime
import java.util.*

class OrderServiceImpl(
    private val orderService: OrderService,
): OrderServiceGrpc.OrderServiceImplBase() {

    override fun getOrder(request: GetOrderPayload, responseObserver: StreamObserver<Order>) {
        val order = try {
            orderService.getOrder(UUID.fromString(request.id))
        } catch (e: OrderNotFoundException) {
            responseObserver.onError(Status.NOT_FOUND.withCause(e).asRuntimeException())
            return
        }
        responseObserver.onNext(order.toAPI())
        responseObserver.onCompleted()
    }

    override fun createOrder(request: CreateOrderPayload, responseObserver: StreamObserver<Order>) {
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
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e).withDescription("invalid restaurant id").asRuntimeException())
            return
        } catch (e: InvalidMenuItemIdException) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withCause(e).withDescription("invalid menuItemId ${e.menuItemId}").asRuntimeException())
            return
        }
        responseObserver.onNext(order.toAPI())
        responseObserver.onCompleted()
    }
}
