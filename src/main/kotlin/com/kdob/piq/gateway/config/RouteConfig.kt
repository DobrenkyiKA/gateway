package com.kdob.piq.gateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RouteConfig {

    companion object {
        private const val API_PREFIX_REWRITE = "/api(?<segment>/?.*)"
        private const val SEGMENT_REPLACEMENT = "\${segment}"
    }

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator = builder.routes {

        route("auth-server") {
            path("/api/auth/**")
            filters { rewritePath(API_PREFIX_REWRITE, SEGMENT_REPLACEMENT) }
            uri("lb://auth-server")
        }

        route("user") {
            path("/api/users/**")
            filters { rewritePath(API_PREFIX_REWRITE, SEGMENT_REPLACEMENT) }
            uri("lb://user")
        }

        route("content") {
            path(
                "/api/domains/**",
                "/api/topics/**",
                "/api/questions/**",
                "/api/search/**",
                "/api/srs/**",
                "/api/progress/**"
            )
            filters { rewritePath(API_PREFIX_REWRITE, SEGMENT_REPLACEMENT) }
            uri("lb://content")
        }

        route("ai") {
            path("/api/pipeline/**")
            filters { rewritePath(API_PREFIX_REWRITE, SEGMENT_REPLACEMENT) }
            uri("lb://ai")
        }

        route("storage") {
            path("/api/versions/**")
            filters { rewritePath(API_PREFIX_REWRITE, SEGMENT_REPLACEMENT) }
            uri("lb://storage")
        }
    }
}