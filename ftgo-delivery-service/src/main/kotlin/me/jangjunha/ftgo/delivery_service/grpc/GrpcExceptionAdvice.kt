package me.jangjunha.ftgo.delivery_service.grpc

import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.LoggerFactory

@GrpcAdvice
class GrpcExceptionAdvice {

    private val logger = LoggerFactory.getLogger(GrpcExceptionAdvice::class.java)

    @GrpcExceptionHandler(Throwable::class)
    fun handleInternalError(e: Throwable): StatusException {
        logger.error("Error occurred during process request", e)
        return Status.INTERNAL.withCause(e).withDescription("Internal server error").asException()
    }
}
