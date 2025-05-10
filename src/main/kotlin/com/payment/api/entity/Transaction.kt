package com.payment.api.entity

import jakarta.persistence.*
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull(message = "사용자 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @NotNull(message = "거래 금액은 필수입니다")
    @DecimalMin(value = "0.01", message = "거래 금액은 0.01 이상이어야 합니다")
    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @NotNull(message = "거래 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: TransactionType,

    @NotNull(message = "거래 상태는 필수입니다")
    @Column(nullable = false)
    val status: String,

    @Column
    val description: String? = null,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    PAYMENT,    // 결제
    TOP_UP,     // 충전
    REFUND,     // 환불
    WITHDRAWAL  // 출금
} 