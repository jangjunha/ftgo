package me.jangjunha.ftgo.order_service.grpc

import io.grpc.Server
import io.grpc.ServerBuilder
import io.grpc.ServerInterceptors
import me.jangjunha.ftgo.common.auth.AuthInterceptor
import me.jangjunha.ftgo.order_service.service.OrderService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.SmartLifecycle
import java.io.IOException

class GrpcServer(
    private val port: Int,
    private val orderService: OrderService,
    private val server: Server = ServerBuilder.forPort(port)
        .addService(
            ServerInterceptors.intercept(
                OrderServiceImpl(orderService),
                AuthInterceptor()
            )
        )
        .build(),
) :
    SmartLifecycle {
    private val logger: Logger = LoggerFactory.getLogger(GrpcServer::class.java)

    override fun start() {
        try {
            server.start()
        } catch (e: IOException) {
            logger.error("gRPC server raises error", e)
            throw RuntimeException(e)
        }
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
