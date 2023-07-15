package me.jangjunha.ftgo.order_service.grpc

import io.grpc.stub.StreamObserver
import me.jangjunha.ftgo.order_service.api.GetOrderPayload
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpc;
import me.jangjunha.ftgo.order_service.domain.OrderNotFoundException
import me.jangjunha.ftgo.order_service.service.OrderService
import java.util.*

class OrderServiceImpl(
    private val orderService: OrderService,
): OrderServiceGrpc.OrderServiceImplBase() {

    override fun getOrder(request: GetOrderPayload, responseObserver: StreamObserver<Order>) {
        val order = try {
            orderService.getOrder(UUID.fromString(request.id))
        } catch (e: OrderNotFoundException) {
            responseObserver.onError(e)
            return
        }
        responseObserver.onNext(order.toAPI())
        responseObserver.onCompleted()
    }
}
