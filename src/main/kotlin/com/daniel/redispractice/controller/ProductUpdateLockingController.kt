package com.daniel.redispractice.controller

import com.daniel.redispractice.dto.ProductDTO
import com.daniel.redispractice.dto.ProductUpdateRequest
import com.daniel.redispractice.locking.RedisLockManager
import com.daniel.redispractice.mapper.ProductDtoMapper
import com.daniel.redispractice.service.DbProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.time.Duration

@RestController
@RequestMapping("/products/lock")
class ProductUpdateLockingController(
    private val lockManager: RedisLockManager,
    private val dbProductService: DbProductService,
    private val mapper: ProductDtoMapper
) {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody request: ProductUpdateRequest,
        @RequestParam(defaultValue = "500") workMs: Long
    ): ResponseEntity<ProductDTO> {
        log.info("Updating product with locking $id")
        val lockKey = "product:$id"
        val lockId = lockManager.tryLock(lockKey, Duration.ofMinutes(1))
            ?: throw ResponseStatusException(
                HttpStatus.LOCKED,
                "Locked for product with id $id, try later"
            )

        try {
            try {
                Thread.sleep(workMs)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
            val product = dbProductService.update(id, request)
            val productDto = mapper.toProductDto(product)
            log.info("Product $id has been updated")
            return ResponseEntity.ok(productDto)
        } finally {
            lockManager.unlock(lockKey, lockId)
        }
    }
}
