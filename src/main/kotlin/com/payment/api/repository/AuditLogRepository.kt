package com.payment.api.repository

import com.payment.api.entity.AuditLog
import com.payment.api.entity.AuditLogType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface AuditLogRepository : JpaRepository<AuditLog, Long> {
    fun findByUserId(userId: Long): List<AuditLog>
    fun findByType(type: AuditLogType): List<AuditLog>

    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate")
    fun findByTimestampBetween(
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<AuditLog>

    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId AND a.timestamp BETWEEN :startDate AND :endDate")
    fun findByUserIdAndTimestampBetween(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<AuditLog>

    fun findByUserId(userId: Long, pageable: Pageable): Page<AuditLog>
    fun findByType(type: AuditLogType, pageable: Pageable): Page<AuditLog>
} 