package com.daniel.redispractice.service

import com.daniel.redispractice.dto.ProductCreateRequest
import com.daniel.redispractice.dto.ProductUpdateRequest
import com.daniel.redispractice.entity.ProductEntity

interface ProductService {
    fun create(product: ProductCreateRequest): ProductEntity
    fun update(id: Long, updateRequest: ProductUpdateRequest): ProductEntity
    fun getById(productId: Long): ProductEntity
    fun delete(productId: Long)
}