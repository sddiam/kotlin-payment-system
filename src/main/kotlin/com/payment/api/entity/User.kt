package com.payment.api.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @NotBlank(message = "이메일은 필수 입력값입니다")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @Column(unique = true, nullable = false)
    val email: String,

    @NotBlank(message = "비밀번호는 필수 입력값입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Column(nullable = false)
    val password: String,

    @NotBlank(message = "이름은 필수 입력값입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    @Column(nullable = false)
    val name: String,

    @NotBlank(message = "전화번호는 필수 입력값입니다")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "유효한 전화번호를 입력해주세요")
    @Column(nullable = false)
    val phoneNumber: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val isActive: Boolean = true,

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val balance: Balance? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val transactions: List<Transaction> = emptyList(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val auditLogs: List<AuditLog> = emptyList()
) 