package me.jangjunha.ftgo.common.auth

import io.grpc.CallCredentials
import io.grpc.Metadata
import java.util.concurrent.Executor

open class ExplicitCallCredentials(
    private val key: String,
    private val value: String,
) : CallCredentials() {
    override fun applyRequestMetadata(requestInfo: RequestInfo, appExecutor: Executor, applier: MetadataApplier) {
        appExecutor.execute {
            applier.apply(Metadata().apply {
                put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
            })
        }
    }
}
