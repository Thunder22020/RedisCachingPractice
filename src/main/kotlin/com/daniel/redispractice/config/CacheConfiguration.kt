package com.daniel.redispractice.config

import com.daniel.redispractice.entity.ProductEntity
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import tools.jackson.databind.ObjectMapper
import java.time.Duration

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun redisTemplate(
        redisConnectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): RedisTemplate<String, ProductEntity> =
        RedisTemplate<String, ProductEntity>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = JacksonJsonRedisSerializer(objectMapper, ProductEntity::class.java)
            afterPropertiesSet()
        }

    @Bean
    fun cacheManager(
        redisConnectionFactory: RedisConnectionFactory,
        objectMapper: ObjectMapper,
    ): CacheManager {

        val jsonSerializer = JacksonJsonRedisSerializer(objectMapper, ProductEntity::class.java)

        val config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(1))
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
            )

        return RedisCacheManager.builder(redisConnectionFactory)
            .cacheDefaults(config)
            .transactionAware()
            .build()
    }
}