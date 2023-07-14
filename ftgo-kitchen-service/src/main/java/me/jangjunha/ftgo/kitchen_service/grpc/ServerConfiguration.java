package me.jangjunha.ftgo.kitchen_service.grpc;

import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConfiguration {

    @Value("${grpc.server.port}")
    private int grpcServerPort;

    @Bean
    public GrpcServer grpcServer(KitchenService kitchenService) {
        return new GrpcServer(grpcServerPort, kitchenService);
    }
}
