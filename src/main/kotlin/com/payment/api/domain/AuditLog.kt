package com.payment.api.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "audit_logs")
data class AuditLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Column(nullable = false)
    val action: String,

    @Column(nullable = false)
    val details: String,

    @Column(nullable = false)
    val ipAddress: String,

    @Column(nullable = false)
    val userAgent: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) 