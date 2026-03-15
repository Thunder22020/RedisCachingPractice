package com.daniel.redispractice.mapper

import com.daniel.redispractice.dto.ProductDTO
import com.daniel.redispractice.entity.ProductEntity
import org.springframework.stereotype.Component

@Component
class ProductDtoMapper {
    fun toProductDto(product: ProductEntity): ProductDTO {
        return ProductDTO(
            id = requireNotNull(product.id),
            name = product.name,
            description = product.description,
            price = product.price,
            updatedAt = requireNotNull(product.updateAt),
            createdAt = product.createdAt
        )
    }

    fun toProductEntity(dto: ProductDTO): ProductEntity {
        return ProductEntity(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            price = dto.price,
        )
    }
}
