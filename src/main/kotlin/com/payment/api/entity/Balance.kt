package com.payment.api.entity

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "balances")
data class Balance(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull(message = "사용자 정보는 필수입니다")
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    val user: User,

    @NotNull(message = "잔액은 필수입니다")
    @DecimalMin(value = "0.0", message = "잔액은 0 이상이어야 합니다")
    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false)
    val lastUpdated: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 