package com.kdob.piq.gateway.config

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RouteConfig {
    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator = builder.routes {

        // ────────────────────────────────────────────
        // Backend API Routes
        // All strip the /api prefix before forwarding
        // ────────────────────────────────────────────

        route("auth-server") {
            path("/api/auth/**")
            filters {
                rewritePath("/api(?<segment>/?.*)", "\${segment}")
            }
            uri("lb://auth-server")
        }

        route("user") {
            path("/api/users/**")
            filters {
                rewritePath("/api(?<segment>/?.*)", "\${segment}")
            }
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
            filters {
                rewritePath("/api(?<segment>/?.*)", "\${segment}")
            }
            uri("lb://content")
        }

        route("ai") {
            path("/api/pipeline/**")
            filters {
                rewritePath("/api(?<segment>/?.*)", "\${segment}")
            }
            uri("lb://ai")
        }

        route("storage") {
            path("/api/storage/**")
            filters {
                rewritePath("/api(?<segment>/?.*)", "\${segment}")
            }
            uri("lb://storage")
        }

        // ────────────────────────────────────────────
        // Frontend Routes
        // No path rewriting — frontends handle their own routing
        // ────────────────────────────────────────────

        route("admin-ui") {
            path("/admin/**")
            uri("lb://admin-ui")
        }

        route("content-ui") {
            path(
                "/domains/**",
                "/learn/**",
                "/progress/**",
                "/search/**"
            )
            uri("lb://content-ui")
        }

        // Home UI — catch-all, must be last
        route("home-ui") {
            path("/**")
            uri("lb://home-ui")
        }
    }
}