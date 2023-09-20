package me.jangjunha.ftgo.apigateway.proxies

import io.grpc.CallCredentials
import io.grpc.ManagedChannelBuilder
import me.jangjunha.ftgo.apigateway.Destinations
import me.jangjunha.ftgo.order_service.api.Order
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpcKt
import me.jangjunha.ftgo.order_service.api.getOrderPayload
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

    suspend fun findOrderById(id: UUID, credentials: CallCredentials): Order {
        val payload = getOrderPayload {
            this.id = id.toString()
        }
        return stub.withCallCredentials(credentials).getOrder(payload)
    }
}
