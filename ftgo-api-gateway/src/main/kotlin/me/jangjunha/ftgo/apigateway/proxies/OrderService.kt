package me.jangjunha.ftgo.apigateway.proxies

import io.grpc.ManagedChannelBuilder
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.order_service.api.GetOrderPayload
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpcKt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OrderService
@Autowired constructor(
    destinations: Destinations,
) {
    private val stub: OrderServiceGrpcKt.OrderServiceCoroutineStub = OrderServiceGrpcKt.OrderServiceCoroutineStub(
        ManagedChannelBuilder.forTarget(destinations.orderServiceUrl).usePlaintext().build()
    )

    suspend fun findOrderById(id: UUID): Order {
        val payload = GetOrderPayload.newBuilder().setId(id.toString()).build()
        return stub.getOrder(payload)
    }
}
