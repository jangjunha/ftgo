package me.jangjunha.ftgo.apigateway.security.grpc

import io.grpc.CallCredentials
import io.grpc.Metadata
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.util.concurrent.Executor

class ForwardCallCredentials(
    private val securityContext: SecurityContext,
) : CallCredentials() {
    override fun applyRequestMetadata(requestInfo: RequestInfo, appExecutor: Executor, applier: MetadataApplier) {
        appExecutor.execute {
            val jwt = securityContext.authentication as JwtAuthenticationToken
            val metadata = jwt.tokenAttributes
                .filterKeys { it.startsWith(PREFIX) }
                .mapKeys { it.key.removePrefix(PREFIX) }
                .mapKeys { "x-ftgo-authenticated-${it.key}" }
                .mapValues { it.value as String }
                .toMap()
            applier.apply(Metadata().apply {
                for ((key, value) in metadata) {
                    put(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER), value)
                }
            })
        }
    }

    companion object {
        private const val PREFIX = "https://ftgo.jangjunha.me/auth/"
    }
}
