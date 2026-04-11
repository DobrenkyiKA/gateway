package com.kdob.piq.gateway.filter

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class RequestLoggingFilter : GlobalFilter, Ordered {

    private val logger = LoggerFactory.getLogger(RequestLoggingFilter::class.java)

    override fun getOrder(): Int = -1 // Runs before AuthHeaderRelayFilter

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val startTime = System.currentTimeMillis()

        logger.info(
            "→ {} {} from {}",
            request.method,
            request.uri.path,
            request.remoteAddress?.address?.hostAddress ?: "unknown"
        )

        return chain.filter(exchange).then(
            Mono.fromRunnable {
                val duration = System.currentTimeMillis() - startTime
                val statusCode = exchange.response.statusCode?.value() ?: "unknown"
                logger.info(
                    "← {} {} → {} ({}ms)",
                    request.method,
                    request.uri.path,
                    statusCode,
                    duration
                )
            }
        )
    }
}