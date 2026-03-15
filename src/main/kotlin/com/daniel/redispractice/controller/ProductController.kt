package com.daniel.redispractice.controller

import com.daniel.redispractice.dto.ProductCreateRequest
import com.daniel.redispractice.dto.ProductDTO
import com.daniel.redispractice.dto.ProductUpdateRequest
import com.daniel.redispractice.enum.CacheMode
import com.daniel.redispractice.mapper.ProductDtoMapper
import com.daniel.redispractice.service.DbProductService
import com.daniel.redispractice.service.ManualCachingProductService
import com.daniel.redispractice.service.SpringCachingProductService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/products")
class ProductController(
    val dbProductService: DbProductService,
    val manualCachingProductService: ManualCachingProductService,
    val springProductService: SpringCachingProductService,
    val mapper: ProductDtoMapper
) {
    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    @PostMapping
    fun save(
        @RequestBody request: ProductCreateRequest,
        @RequestParam(value="cacheMode", defaultValue = "NONE_CACHE") cacheMode: CacheMode,
    ): ResponseEntity<ProductDTO> {
        log.info("Creating product with cacheMode={}", cacheMode)

        val service = resolveProductService(cacheMode)
        val product = service.create(request)
        val dto = mapper.toProductDto(product)
        return ResponseEntity.status(HttpStatus.CREATED).body(dto)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: Long,
        @RequestParam(value="cacheMode", defaultValue = "NONE_CACHE") cacheMode: CacheMode,
    ): ResponseEntity<ProductDTO> {
        log.info("Get product {} with cacheMode={}", id, cacheMode)
        val service = resolveProductService(cacheMode)
        val product = service.getById(id)
        val dto = mapper.toProductDto(product)
        return ResponseEntity.ok(dto)
    }

    @PutMapping("/{id}")
    fun updateById(
        @PathVariable id: Long,
        @RequestBody request: ProductUpdateRequest,
        @RequestParam(value="cacheMode", defaultValue = "NONE_CACHE") cacheMode: CacheMode,
    ): ResponseEntity<ProductDTO> {
        val service = resolveProductService(cacheMode)
        val product = service.update(id, request)
        val dto = mapper.toProductDto(product)
        return ResponseEntity.ok(dto)
    }

    @DeleteMapping("/{id}")
    fun deleteById(
        @PathVariable id: Long,
        @RequestParam(value="cacheMode", defaultValue = "NONE_CACHE") cacheMode: CacheMode
    ) {
        val service = resolveProductService(cacheMode)
        service.delete(id)
    }

    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductDTO>> {
        val products = dbProductService.getAllProducts()
        val productsDto = products.map { mapper.toProductDto(it) }
        return ResponseEntity.ok(productsDto)
    }

    private fun resolveProductService(cacheMode: CacheMode) = when (cacheMode) {
        CacheMode.MANUAL -> manualCachingProductService
        CacheMode.NONE_CACHE -> dbProductService
        CacheMode.SPRING -> springProductService
    }
}
