package com.payment.api.repository

import com.payment.api.entity.Transaction
import com.payment.api.entity.TransactionType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDateTime

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findByUserId(userId: Long): List<Transaction>
    fun findByUserIdAndType(userId: Long, type: TransactionType): List<Transaction>
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.timestamp BETWEEN :startDate AND :endDate")
    fun findByUserIdAndTimestampBetween(
        @Param("userId") userId: Long,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): List<Transaction>

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = :type AND t.timestamp BETWEEN :startDate AND :endDate")
    fun sumAmountByUserIdAndTypeAndTimestampBetween(
        @Param("userId") userId: Long,
        @Param("type") type: TransactionType,
        @Param("startDate") startDate: LocalDateTime,
        @Param("endDate") endDate: LocalDateTime
    ): BigDecimal

    fun findByUserId(userId: Long, pageable: Pageable): Page<Transaction>
} 