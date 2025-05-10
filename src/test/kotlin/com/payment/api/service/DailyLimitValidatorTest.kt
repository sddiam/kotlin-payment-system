package com.payment.api.service

import com.payment.api.entity.TransactionType
import com.payment.api.repository.TransactionRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.LocalTime

class DailyLimitValidatorTest {
    private lateinit var dailyLimitValidator: DailyLimitValidator
    private lateinit var transactionRepository: TransactionRepository

    @BeforeEach
    fun setup() {
        transactionRepository = mockk()
        dailyLimitValidator = DailyLimitValidator(transactionRepository)
    }

    @Test
    fun `validateDailyLimit should pass when payment amount is within limit`() {
        // Given
        val userId = 1L
        val amount = BigDecimal("1000000")
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        coEvery { 
            transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
                userId = userId,
                type = TransactionType.PAYMENT,
                startDate = today,
                endDate = tomorrow
            )
        } returns BigDecimal("2000000")

        // When & Then
        dailyLimitValidator.validateDailyLimit(userId, amount, TransactionType.PAYMENT)
    }

    @Test
    fun `validateDailyLimit should throw exception when payment amount exceeds daily limit`() {
        // Given
        val userId = 1L
        val amount = BigDecimal("4000000")
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        coEvery { 
            transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
                userId = userId,
                type = TransactionType.PAYMENT,
                startDate = today,
                endDate = tomorrow
            )
        } returns BigDecimal("2000000")

        // When & Then
        assertThrows<IllegalArgumentException> {
            dailyLimitValidator.validateDailyLimit(userId, amount, TransactionType.PAYMENT)
        }
    }

    @Test
    fun `validateDailyLimit should pass when top-up amount is within limit`() {
        // Given
        val userId = 1L
        val amount = BigDecimal("5000000")
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        coEvery { 
            transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
                userId = userId,
                type = TransactionType.TOP_UP,
                startDate = today,
                endDate = tomorrow
            )
        } returns BigDecimal("10000000")

        // When & Then
        dailyLimitValidator.validateDailyLimit(userId, amount, TransactionType.TOP_UP)
    }

    @Test
    fun `validateDailyLimit should throw exception when top-up amount exceeds daily limit`() {
        // Given
        val userId = 1L
        val amount = BigDecimal("15000000")
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        coEvery { 
            transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
                userId = userId,
                type = TransactionType.TOP_UP,
                startDate = today,
                endDate = tomorrow
            )
        } returns BigDecimal("10000000")

        // When & Then
        assertThrows<IllegalArgumentException> {
            dailyLimitValidator.validateDailyLimit(userId, amount, TransactionType.TOP_UP)
        }
    }

    @Test
    fun `getDailyTotal should return correct sum for payment transactions`() {
        // Given
        val userId = 1L
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)
        val expectedTotal = BigDecimal("3000000")

        coEvery { 
            transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
                userId = userId,
                type = TransactionType.PAYMENT,
                startDate = today,
                endDate = tomorrow
            )
        } returns expectedTotal

        // When
        val result = dailyLimitValidator.getDailyTotal(userId, TransactionType.PAYMENT)

        // Then
        assert(result == expectedTotal)
    }

    @Test
    fun `getDailyTotal should return zero when no transactions exist`() {
        // Given
        val userId = 1L
        val today = LocalDateTime.now().with(LocalTime.MIN)
        val tomorrow = today.plusDays(1)

        coEvery { 
            transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
                userId = userId,
                type = TransactionType.PAYMENT,
                startDate = today,
                endDate = tomorrow
            )
        } returns null

        // When
        val result = dailyLimitValidator.getDailyTotal(userId, TransactionType.PAYMENT)

        // Then
        assert(result == BigDecimal.ZERO)
    }
} 