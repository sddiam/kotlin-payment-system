package com.payment.api.service

import com.payment.api.entity.AuditLog
import com.payment.api.entity.User
import com.payment.api.repository.AuditLogRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class AuditLogServiceTest {
    private lateinit var auditLogService: AuditLogService
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var user: User

    @BeforeEach
    fun setup() {
        auditLogRepository = mockk()
        auditLogService = AuditLogService(auditLogRepository)

        user = User(
            email = "test@example.com",
            password = "password",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        user.id = 1L
    }

    @Test
    fun `logUserAction should create audit log with success status`() {
        // Given
        val action = "TEST_ACTION"
        val details = "Test details"
        val auditLog = AuditLog(
            user = user,
            action = action,
            details = details,
            status = "SUCCESS",
            timestamp = any()
        )

        coEvery { auditLogRepository.save(any()) } returns auditLog

        // When
        val result = auditLogService.logUserAction(user, action, details)

        // Then
        assert(result.action == action)
        assert(result.details == details)
        assert(result.status == "SUCCESS")
        assert(result.user == user)

        coVerify { auditLogRepository.save(any()) }
    }

    @Test
    fun `logTransaction should create transaction audit log`() {
        // Given
        val transactionId = 1L
        val amount = "1000"
        val type = "PAYMENT"
        val status = "COMPLETED"
        val auditLog = AuditLog(
            user = user,
            action = "TRANSACTION",
            details = "Transaction ID: $transactionId, Amount: $amount, Type: $type",
            status = status,
            timestamp = any()
        )

        coEvery { auditLogRepository.save(any()) } returns auditLog

        // When
        val result = auditLogService.logTransaction(user, transactionId, amount, type, status)

        // Then
        assert(result.action == "TRANSACTION")
        assert(result.details.contains("Transaction ID: $transactionId"))
        assert(result.details.contains("Amount: $amount"))
        assert(result.details.contains("Type: $type"))
        assert(result.status == status)

        coVerify { auditLogRepository.save(any()) }
    }

    @Test
    fun `logBalanceChange should create balance change audit log`() {
        // Given
        val previousBalance = "1000"
        val newBalance = "2000"
        val change = "1000"
        val auditLog = AuditLog(
            user = user,
            action = "BALANCE_CHANGE",
            details = "Previous: $previousBalance, New: $newBalance, Change: $change",
            status = "SUCCESS",
            timestamp = any()
        )

        coEvery { auditLogRepository.save(any()) } returns auditLog

        // When
        val result = auditLogService.logBalanceChange(user, previousBalance, newBalance, change)

        // Then
        assert(result.action == "BALANCE_CHANGE")
        assert(result.details.contains("Previous: $previousBalance"))
        assert(result.details.contains("New: $newBalance"))
        assert(result.details.contains("Change: $change"))
        assert(result.status == "SUCCESS")

        coVerify { auditLogRepository.save(any()) }
    }

    @Test
    fun `logError should create error audit log`() {
        // Given
        val action = "TEST_ACTION"
        val errorMessage = "Test error message"
        val auditLog = AuditLog(
            user = user,
            action = action,
            details = errorMessage,
            status = "ERROR",
            timestamp = any()
        )

        coEvery { auditLogRepository.save(any()) } returns auditLog

        // When
        val result = auditLogService.logError(user, action, errorMessage)

        // Then
        assert(result.action == action)
        assert(result.details == errorMessage)
        assert(result.status == "ERROR")

        coVerify { auditLogRepository.save(any()) }
    }
} 