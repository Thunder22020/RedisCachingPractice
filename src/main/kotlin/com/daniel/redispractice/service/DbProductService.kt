package com.daniel.redispractice.service

import com.daniel.redispractice.controller.ProductController
import com.daniel.redispractice.dto.ProductCreateRequest
import com.daniel.redispractice.dto.ProductUpdateRequest
import com.daniel.redispractice.entity.ProductEntity
import com.daniel.redispractice.repository.ProductRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DbProductService(
    val productRepository: ProductRepository,
) : ProductService {

    private val log: Logger = LoggerFactory.getLogger(ProductController::class.java)

    override fun create(product: ProductCreateRequest): ProductEntity {
        log.info("Creating product in DB")

        val product = ProductEntity(
            id = null,
            name = product.name,
            price = product.price,
            description = product.description,
        )
        return productRepository.save(product)
    }

    override fun update(id: Long, updateRequest: ProductUpdateRequest): ProductEntity {
        log.info("Updating product {} from DB", id)

        val product = productRepository.findById(id).orElseThrow {
            RuntimeException("Product with ID $id not found")
        }
        updateRequest.description?.let {
            product.description = it
        }
        updateRequest.price?.let {
            product.price = it
        }
        return productRepository.save(product)
    }

    override fun getById(productId: Long): ProductEntity {
        log.info("Get product {} from DB", productId)

        return productRepository.findById(productId).orElseThrow {
            RuntimeException("Product with ID $productId not found")
        }
    }

    override fun delete(productId: Long) {
        log.info("Deleting product {} from DB", productId)

        productRepository.deleteById(productId)
    }

    fun getAllProducts(): List<ProductEntity> {
        log.info("Get all products from DB")

        return productRepository.findAll()
    }
}