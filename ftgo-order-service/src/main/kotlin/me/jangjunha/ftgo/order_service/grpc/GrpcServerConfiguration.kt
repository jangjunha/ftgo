package me.jangjunha.ftgo.order_service.grpc

import me.jangjunha.ftgo.order_service.service.OrderService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GrpcServerConfiguration {
    @Value("\${grpc.server.port}")
    private val grpcServerPort = 8107

    @Bean
    fun grpcServer(orderService: OrderService): GrpcServer {
        return GrpcServer(grpcServerPort, orderService)
    }
}
