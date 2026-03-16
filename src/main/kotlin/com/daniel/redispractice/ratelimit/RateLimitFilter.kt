package com.daniel.redispractice.ratelimit

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RateLimitFilter(
    private val rateLimiter: FixedWindowRateLimiter
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        var client = request.getHeader("X-API-KEY")
        if (client == null) {
            client = request.remoteAddr
        }

        val allowed = rateLimiter.allowRequest(
            client,
            50,
            Duration.ofSeconds(20)
        )

        if (!allowed) {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write("Rate limit exceeded\n")
            return
        }

        filterChain.doFilter(request, response)
    }
}
