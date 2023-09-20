package me.jangjunha.ftgo.apigateway.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.jwt.*
import org.springframework.security.web.server.SecurityWebFilterChain


@EnableWebFluxSecurity
class SecurityConfig {

    @Value("\${auth0.audience}")
    private lateinit var audience: String

    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private lateinit var issuer: String

    @Bean
    fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain = http {
        authorizeExchange {
            authorize("/orders/**", authenticated)
        }
        cors { }
        oauth2ResourceServer {
            jwt { }
        }
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        /*
        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
        indeed intended for our app. Adding our own validator is easy to do:
        */
        val jwtDecoder = JwtDecoders.fromOidcIssuerLocation<JwtDecoder>(issuer) as NimbusJwtDecoder
        val audienceValidator = AudienceValidator(audience)
        val withIssuer = JwtValidators.createDefaultWithIssuer(issuer)
        val withAudience = DelegatingOAuth2TokenValidator(withIssuer, audienceValidator)
        jwtDecoder.setJwtValidator(withAudience)
        return jwtDecoder
    }
}
