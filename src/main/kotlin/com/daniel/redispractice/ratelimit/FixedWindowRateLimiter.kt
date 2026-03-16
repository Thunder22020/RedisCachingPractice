package com.daniel.redispractice.ratelimit

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class FixedWindowRateLimiter(
    private val redisTemplate: StringRedisTemplate,
) {
    fun allowRequest(
        clientId: String,
        limit: Int,
        windowSize: Duration
    ) : Boolean {
        val windowIndex = System.currentTimeMillis() / windowSize.toMillis()
        val key = "rate:${clientId}:${windowIndex}"

        val countHits = redisTemplate.opsForValue().increment(key)

        if (countHits != null && countHits == 1L) {
            redisTemplate.expire(key, windowSize)
        }

        return countHits != null && countHits <= limit
    }
}
