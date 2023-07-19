package me.jangjunha.ftgo.kitchen_service.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import me.jangjunha.ftgo.kitchen_service.service.KitchenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import java.io.IOException;

public class GrpcServer implements SmartLifecycle {

    private final Logger logger = LoggerFactory.getLogger(GrpcServer.class);
    private final Server server;
    private final int port;

    public GrpcServer(int grpcServerPort, KitchenService kitchenService) {
        this.port = grpcServerPort;
        this.server = ServerBuilder.forPort(port)
                .addService(new KitchenServiceImpl(kitchenService))
                .build();
    }

    @Override
    public void start() {
        try {
            server.start();
        } catch (IOException e) {
            logger.error("gRPC server raises error", e);
            throw new RuntimeException(e);
        }
        logger.info("gRPC server started, listening on %d".formatted(port));
    }

    @Override
    public void stop() {
        if (server != null) {
            logger.info("*** shutting down gRPC server since JVM is shutting down");
            server.shutdown();
            logger.info("*** gRPC server shut down");
        }
    }

    @Override
    public boolean isRunning() {
        return server.isTerminated();
    }
}