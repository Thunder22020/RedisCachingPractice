package com.daniel.redispractice.service

import com.daniel.redispractice.controller.ProductController
import com.daniel.redispractice.dto.ProductCreateRequest
import com.daniel.redispractice.dto.ProductUpdateRequest
import com.daniel.redispractice.entity.ProductEntity
import com.daniel.redispractice.repository.ProductRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ManualCachingProductService(
    private val productRepository: ProductRepository,
    private val redisTemplate: RedisTemplate<String, ProductEntity>,
) : ProductService {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    override fun create(product: ProductCreateRequest): ProductEntity {
        log.info("Creating product with cacheMode={}", "none-cache")

        val product = ProductEntity(
            id = null,
            name = product.name,
            price = product.price,
            description = product.description,
        )
        return productRepository.save(product)
    }

    override fun update(id: Long, updateRequest: ProductUpdateRequest): ProductEntity {
        log.info("Updating product {}, using caching", id)

        val product = productRepository.findById(id).orElseThrow {
            RuntimeException("Product with ID $id not found")
        }
        updateRequest.description?.let {
            product.description = it
        }
        updateRequest.price?.let {
            product.price = it
        }
        val entity = productRepository.save(product)
        redisTemplate.delete(CACHE_PREFIX + id)
        log.info("Cache invalidated for deleted product {}", id)
        return entity
    }

    override fun getById(productId: Long): ProductEntity {
        log.info("Get product {}", productId)
        val cacheKey = CACHE_PREFIX + productId

        val objFromCache = redisTemplate.opsForValue().get(cacheKey)
        if (objFromCache != null) {
            log.info("Product {} found in cache", productId)
            return objFromCache
        }

        log.info("Product {} not found in cache", productId)

        val entityFromDb: ProductEntity = productRepository.findById(productId).orElseThrow {
            RuntimeException("Product with ID $productId not found")
        }

        redisTemplate.opsForValue()
            .set(cacheKey, entityFromDb, CACHE_TTL_MINUTES, TimeUnit.MINUTES)
        log.info("Product {} was cached", productId)

        return entityFromDb
    }

    override fun delete(productId: Long) {
        log.info("Deleting product {}, using CACHE", productId)

        productRepository.deleteById(productId)
        redisTemplate.delete(CACHE_PREFIX + productId)
        log.info("Cache invalidated for deleted product {}", productId)
    }

    fun getAllProducts(): List<ProductEntity> {
        log.info("Get all products")

        return productRepository.findAll()
    }

    companion object {
        private const val CACHE_PREFIX = "product:"
        private const val CACHE_TTL_MINUTES = 30L
    }
}