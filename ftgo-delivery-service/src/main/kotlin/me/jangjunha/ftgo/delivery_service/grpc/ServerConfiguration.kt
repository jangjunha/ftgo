package me.jangjunha.ftgo.delivery_service.grpc

import me.jangjunha.ftgo.delivery_service.domain.DeliveryService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ServerConfiguration {

    @Value("\${grpc.server.port}")
    private val grpcServerPort = 8108

    @Bean
    fun grpcServer(deliveryService: DeliveryService): GrpcServer {
        return GrpcServer(grpcServerPort, deliveryService)
    }
}
