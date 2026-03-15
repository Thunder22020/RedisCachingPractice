package com.daniel.redispractice.dto

import java.math.BigDecimal

data class ProductUpdateRequest(
    var price: BigDecimal?,
    var description: String?,
)
