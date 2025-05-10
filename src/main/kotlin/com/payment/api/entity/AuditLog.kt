package com.payment.api.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

@Entity
@Table(name = "audit_logs")
data class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotNull(message = "사용자 정보는 필수입니다")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @NotBlank(message = "액션은 필수입니다")
    @Column(nullable = false)
    val action: String,

    @NotNull(message = "로그 유형은 필수입니다")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: AuditLogType,

    @NotBlank(message = "상태는 필수입니다")
    @Column(nullable = false)
    val status: String,

    @Column
    val details: String? = null,

    @Column(nullable = false)
    val timestamp: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class AuditLogType {
    USER_ACTION,     // 사용자 액션
    TRANSACTION,     // 거래
    BALANCE_CHANGE,  // 잔액 변경
    ERROR           // 에러
} 