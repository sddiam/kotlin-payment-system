package com.payment.api.repository

import com.payment.api.entity.Balance
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import jakarta.persistence.LockModeType
import java.util.*

@Repository
interface BalanceRepository : JpaRepository<Balance, Long> {
    fun findByUserId(userId: Long): Optional<Balance>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b WHERE b.user.id = :userId")
    fun findByUserIdWithLock(@Param("userId") userId: Long): Optional<Balance>

    fun existsByUserId(userId: Long): Boolean
} 