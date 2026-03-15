package com.daniel.redispractice.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "products")
data class ProductEntity(
    @Id
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "product_id_seq"
    )
    @SequenceGenerator(
        name = "product_id_seq",
        sequenceName = "product_id_seq",
        allocationSize = 1
    )
    var id: Long? = null,

    @Column(nullable = false)
    var price: BigDecimal,

    @Column(nullable = false)
    var name: String = "",

    var description: String,

    @Column(nullable = false, updatable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var updateAt: Instant? = null,
) {
    @PreUpdate
    fun preUpdate() {
        this.updateAt = Instant.now()
    }

    @PrePersist
    fun prePersist() {
        this.updateAt = Instant.now()
        this.createdAt = Instant.now()
    }
}
