package com.payment.api.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false, precision = 19, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: TransactionType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: TransactionStatus = TransactionStatus.PENDING,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class TransactionType {
    DEPOSIT,    // 충전
    WITHDRAWAL, // 출금
    PAYMENT,    // 결제
    REFUND      // 환불
}

enum class TransactionStatus {
    PENDING,    // 대기
    COMPLETED,  // 완료
    FAILED,     // 실패
    CANCELLED   // 취소
} 