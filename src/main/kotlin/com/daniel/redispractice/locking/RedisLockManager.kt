package com.daniel.redispractice.locking

import com.daniel.redispractice.controller.ProductController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.ReturnType
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.UUID

@Service
class RedisLockManager(
    private val redisTemplate: StringRedisTemplate,
) {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    fun tryLock(
        key: String,
        ttl: Duration
    ): String? {
        val lockKey = "lock:$key"
        val lockId = UUID.randomUUID().toString()

        val isLocked = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, lockId, ttl)

        if (isLocked) {
            log.info("Successfully locked for $lockKey and $lockId")
            return lockId
        }
        return null
    }

    fun unlock(
        key: String,
        lockId: String
    ) {
        val lockKey = "lock:$key"

        log.info("Trying to unlock $lockKey and $lockId")

        val script = RedisCallback<Long> { connection ->
            connection.scriptingCommands().eval(
                RELEASE_LOCK_LUA_SCRIPT.toByteArray(StandardCharsets.UTF_8),
                ReturnType.INTEGER,
                1,
                lockKey.toByteArray(),
                lockId.toByteArray()
            )
        }
        val result = redisTemplate.execute(
            script,
            true
        )

        if (result == 1L) {
            log.info("Successfully unlocked for $lockKey and $lockId")
        } else {
            log.info("Lock was already released or re-acquired for $lockKey and $lockId")
        }
    }

    companion object {
        private const val RELEASE_LOCK_LUA_SCRIPT =
            """if redis.call('GET', KEYS[1])==ARGV[1] then return redis.call('DEL', KEYS[1]) else return 0 end"""
    }
}
