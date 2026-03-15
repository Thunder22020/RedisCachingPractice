package com.daniel.redispractice.dto

import java.math.BigDecimal

data class ProductCreateRequest(
    var name: String,
    var price: BigDecimal,
    var description: String,
)
