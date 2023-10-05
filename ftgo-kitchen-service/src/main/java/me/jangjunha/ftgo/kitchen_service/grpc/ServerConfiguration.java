package me.jangjunha.ftgo.kitchen_service.grpc;

import io.grpc.ManagedChannelBuilder;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Destinations.class)
public class ServerConfiguration {

    @Value("${grpc.server.port}")
    private int grpcServerPort;

    @Bean
    public GrpcServer grpcServer(KitchenService kitchenService, OrderServiceGrpc.OrderServiceBlockingStub orderService) {
        return new GrpcServer(grpcServerPort, kitchenService, orderService);
    }

    @Bean
    public OrderServiceGrpc.OrderServiceBlockingStub orderService(Destinations destinations) {
        return OrderServiceGrpc.newBlockingStub(ManagedChannelBuilder.forTarget(destinations.getOrderServiceUrl()).usePlaintext().build());
    }
}
