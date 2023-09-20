package me.jangjunha.ftgo.apigateway.security.grpc

import io.grpc.CallCredentials
import io.grpc.Metadata
import java.util.concurrent.Executor

object ClientCallCredentials : CallCredentials() {
    override fun applyRequestMetadata(requestInfo: RequestInfo, appExecutor: Executor, applier: MetadataApplier) {
        appExecutor.execute {
            applier.apply(Metadata().apply {
                put(
                    Metadata.Key.of("x-ftgo-authenticated-client-id", Metadata.ASCII_STRING_MARSHALLER),
                    "ftgo-api-gateway"
                )
            })
        }
    }
}
