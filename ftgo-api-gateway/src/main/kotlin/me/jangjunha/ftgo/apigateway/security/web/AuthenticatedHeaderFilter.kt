package me.jangjunha.ftgo.apigateway.security.web

import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.server.ServerWebExchange
import reactor.kotlin.core.publisher.switchIfEmpty

object AuthenticatedHeaderFilter : GatewayFilter {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain) =
        ReactiveSecurityContextHolder.getContext()
            .mapNotNull { it.authentication as JwtAuthenticationToken }
            .map { jwt ->
                jwt.tokenAttributes
                    .filterKeys { it.startsWith(PREFIX) }
                    .mapKeys { it.key.removePrefix(PREFIX) }
                    .mapKeys { "x-ftgo-authenticated-${it.key}" }
                    .mapValues { it.value as String }
            }
            .flatMap { headers ->
                val request = exchange.request.mutate().headers {
                    headers.forEach(it::set)
                }.build()
                chain.filter(exchange.mutate().request(request).build())
            }
            .switchIfEmpty { chain.filter(exchange) }

    private const val PREFIX = "https://ftgo.jangjunha.me/auth/"
}
