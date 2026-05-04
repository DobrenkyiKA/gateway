package com.kdob.piq.gateway.filter

import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * Extracts user information from the validated JWT and forwards it
 * to downstream services as trusted HTTP headers.
 *
 * Downstream services don't validate the JWT themselves — the Gateway
 * has already done it. They read these headers instead.
 *
 * Security: these headers are only trustworthy because downstream
 * services are not publicly accessible — only reachable through
 * the Gateway on the internal network.
 */
@Component
class AuthHeaderRelayFilter : GlobalFilter, Ordered {

    companion object {
        const val USER_ID_HEADER = "X-User-Id"
        const val USER_EMAIL_HEADER = "X-User-Email"
        const val USER_ROLES_HEADER = "X-User-Roles"
    }

    override fun getOrder(): Int = 0

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        return ReactiveSecurityContextHolder.getContext()
            .filter { it.authentication is JwtAuthenticationToken }
            .map { context ->
                val jwt = (context.authentication as JwtAuthenticationToken).token
                val mutatedRequest: ServerHttpRequest = exchange.request.mutate()
                    .header(USER_ID_HEADER, jwt.subject)
                    .header(USER_EMAIL_HEADER, jwt.getClaimAsString("email") ?: "")
                    .header(
                        USER_ROLES_HEADER,
                        jwt.getClaimAsStringList("roles")?.joinToString(",") ?: ""
                    )
                    // Remove the Authorization header — downstream services
                    // don't need the raw JWT, only the extracted claims
                    .headers { it.remove("Authorization") }
                    .build()
                exchange.mutate().request(mutatedRequest).build()
            }
            .defaultIfEmpty(exchange) // No auth context — pass through unchanged
            .flatMap { chain.filter(it) }
    }
}