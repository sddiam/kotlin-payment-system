package com.payment.api.service

import com.payment.api.entity.TransactionType
import com.payment.api.repository.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class DailyLimitValidator(
    private val transactionRepository: TransactionRepository
) {
    companion object {
        private val DAILY_PAYMENT_LIMIT = BigDecimal("5000000") // 5백만원
        private val DAILY_TOP_UP_LIMIT = BigDecimal("20000000") // 2천만원
    }

    @Transactional(readOnly = true)
    fun validateDailyLimit(userId: Long, amount: BigDecimal, type: TransactionType) {
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        val dailyTotal = transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
            userId = userId,
            type = type,
            startDate = today,
            endDate = tomorrow
        ) ?: BigDecimal.ZERO

        val limit = when (type) {
            TransactionType.PAYMENT -> DAILY_PAYMENT_LIMIT
            TransactionType.TOP_UP -> DAILY_TOP_UP_LIMIT
        }

        if (dailyTotal.add(amount) > limit) {
            throw IllegalArgumentException(
                "Daily ${type.name.lowercase()} limit exceeded. " +
                "Current daily total: $dailyTotal, " +
                "Attempted amount: $amount, " +
                "Daily limit: $limit"
            )
        }
    }

    @Transactional(readOnly = true)
    fun getDailyTotal(userId: Long, type: TransactionType): BigDecimal {
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        return transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
            userId = userId,
            type = type,
            startDate = today,
            endDate = tomorrow
        ) ?: BigDecimal.ZERO
    }
} 