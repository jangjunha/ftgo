package me.jangjunha.ftgo.order_service.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptors
import io.grpc.health.v1.HealthCheckResponse
import io.grpc.protobuf.services.HealthStatusManager
import me.jangjunha.ftgo.common.auth.AuthInterceptor
import me.jangjunha.ftgo.order_service.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.SmartLifecycle
import java.io.IOException

class GrpcServer(
    private val port: Int,
    orderService: OrderService,
) :
    SmartLifecycle {
    private val logger: Logger = LoggerFactory.getLogger(GrpcServer::class.java)

    private val health: HealthStatusManager = HealthStatusManager()
    private val server: Server = ServerBuilder.forPort(port)
        .addService(
            ServerInterceptors.intercept(
                OrderServiceImpl(orderService),
                AuthInterceptor()
            )
        )
        .addService(health.healthService)
        .build()

    override fun start() {
        try {
            server.start()
        } catch (e: IOException) {
            logger.error("gRPC server raises error", e)
            throw RuntimeException(e)
        }
        health.setStatus("", HealthCheckResponse.ServingStatus.SERVING)
        logger.info("gRPC server started, listening on %d".format(port))
    }

    override fun stop() {
        logger.info("*** shutting down gRPC server since JVM is shutting down")
        server.shutdown()
        logger.info("*** gRPC server shut down")
    }

    override fun isRunning(): Boolean {
        return server.isTerminated
    }
}
