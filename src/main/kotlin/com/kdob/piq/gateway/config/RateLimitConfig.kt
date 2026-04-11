package com.kdob.piq.gateway.config

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Mono

@Configuration
class RateLimitConfig {

    /**
     * Rate limit key strategy:
     * - Authenticated users: rate limit by user ID
     * - Anonymous users: rate limit by IP address
     */
    @Bean
    fun rateLimitKeyResolver(): KeyResolver = KeyResolver { exchange ->
        val userId = exchange.request.headers.getFirst("X-User-Id")
        if (userId != null) {
            Mono.just("user:$userId")
        } else {
            Mono.just(
                "ip:${exchange.request.remoteAddress?.address?.hostAddress ?: "unknown"}"
            )
        }
    }
}