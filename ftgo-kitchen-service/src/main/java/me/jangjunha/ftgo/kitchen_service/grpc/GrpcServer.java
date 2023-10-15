package me.jangjunha.ftgo.kitchen_service.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerInterceptors;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.protobuf.services.HealthStatusManager;
import me.jangjunha.ftgo.common.auth.AuthInterceptor;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import me.jangjunha.ftgo.order_service.api.OrderServiceGrpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;
import java.util.Optional;

public class GrpcServer implements SmartLifecycle {

    private final Logger logger = LoggerFactory.getLogger(GrpcServer.class);
    private Optional<Server> server = Optional.empty();
    private final int port;
    private final HealthStatusManager health = new HealthStatusManager();

    private final KitchenService kitchenService;
    private final OrderServiceGrpc.OrderServiceBlockingStub orderService;


    public GrpcServer(int grpcServerPort, KitchenService kitchenService, OrderServiceGrpc.OrderServiceBlockingStub orderService) {
        this.port = grpcServerPort;
        this.kitchenService = kitchenService;
        this.orderService = orderService;
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }
        Server server = ServerBuilder.forPort(port)
                .addService(ServerInterceptors.intercept(
                        new KitchenServiceImpl(kitchenService, orderService),
                        new AuthInterceptor()
                ))
                .addService(health.getHealthService())
                .build();
        this.server = Optional.of(server);

        try {
            server.start();
        } catch (IOException e) {
            logger.error("gRPC server raises error", e);
            throw new RuntimeException(e);
        }
        health.setStatus("", HealthCheckResponse.ServingStatus.SERVING);
        startAwaitThread();
        logger.info("gRPC server started, listening on %d".formatted(port));
    }

    @Override
    public void stop() {
        server.ifPresent(server -> {
            logger.info("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("*** gRPC server shut down");
        });
    }

    @Override
    public boolean isRunning() {
        return server.map(server -> !server.isTerminated()).orElse(false);
    }

    private void startAwaitThread() {
        server.ifPresent(server -> {
            Thread awaitThread = new Thread(() -> {
                try {
                    server.awaitTermination();
                } catch (InterruptedException e) {
                    logger.error("*** gRPC server awaiter interrupted");
                }
            });
            awaitThread.setName("grpc-server-awaiter");
            awaitThread.setDaemon(false);
            awaitThread.start();
        });
    }
}
