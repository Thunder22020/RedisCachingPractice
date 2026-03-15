package com.daniel.redispractice.dto

import java.math.BigDecimal
import java.time.Instant

data class ProductDTO(
    var id: Long,
    var name: String,
    var description: String,
    var price: BigDecimal,
    val updatedAt: Instant,
    val createdAt: Instant
)
