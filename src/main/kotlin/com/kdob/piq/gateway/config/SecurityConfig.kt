package com.kdob.piq.gateway.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http {
            csrf { disable() }

            authorizeExchange {

                // ── Public frontend routes ──────────────
                authorize("/", permitAll)
                authorize("/login", permitAll)
                authorize("/register", permitAll)
                authorize("/forgot-password", permitAll)
                authorize("/about", permitAll)
                authorize("/domains/**", permitAll)
                authorize("/search/**", permitAll)

                // ── Public API endpoints ────────────────
                authorize("/api/auth/**", permitAll)
//                authorize(HttpMethod.GET, "/api/domains/**", permitAll)
//                authorize(HttpMethod.GET, "/api/topics/**", permitAll)
//                authorize(HttpMethod.GET, "/api/questions/**", permitAll)
//                authorize(HttpMethod.GET, "/api/search/**", permitAll)
                authorize("/api/domains/**", permitAll)
                authorize("/api/topics/**", permitAll)
                authorize("/api/questions/**", permitAll)
                authorize("/api/search/**", permitAll)

                // ── Static assets ───────────────────────
                authorize("/_next/**", permitAll)
                authorize("/favicon.ico", permitAll)
                authorize("/images/**", permitAll)
                authorize("/fonts/**", permitAll)

                // ── Actuator health (for load balancers) ─
                authorize("/actuator/health", permitAll)

                // ── Admin routes ────────────────────────
                authorize("/admin/**", hasRole("ADMIN"))
                authorize("/api/pipeline/**", hasRole("ADMIN"))
                authorize("/api/storage/**", hasRole("ADMIN"))

                // ── Authenticated routes ────────────────
                authorize("/learn/**", authenticated)
                authorize("/progress/**", authenticated)
                authorize("/api/srs/**", authenticated)
                authorize("/api/progress/**", authenticated)
                authorize("/api/users/**", authenticated)
                authorize("/profile/**", authenticated)
                authorize("/settings/**", authenticated)

                // ── Everything else ─────────────────────
                authorize(anyExchange, authenticated)
            }

            oauth2ResourceServer {
                jwt { }
            }
        }
}