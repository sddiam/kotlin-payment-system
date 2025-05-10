package com.payment.api.service

import com.payment.api.entity.AuditLog
import com.payment.api.entity.User
import com.payment.api.repository.AuditLogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuditLogService(
    private val auditLogRepository: AuditLogRepository
) {
    @Transactional
    fun logUserAction(
        user: User,
        action: String,
        details: String,
        status: String = "SUCCESS"
    ): AuditLog {
        val auditLog = AuditLog(
            user = user,
            action = action,
            details = details,
            status = status,
            timestamp = LocalDateTime.now()
        )
        return auditLogRepository.save(auditLog)
    }

    @Transactional
    fun logTransaction(
        user: User,
        transactionId: Long,
        amount: String,
        type: String,
        status: String
    ): AuditLog {
        return logUserAction(
            user = user,
            action = "TRANSACTION",
            details = "Transaction ID: $transactionId, Amount: $amount, Type: $type",
            status = status
        )
    }

    @Transactional
    fun logBalanceChange(
        user: User,
        previousBalance: String,
        newBalance: String,
        change: String
    ): AuditLog {
        return logUserAction(
            user = user,
            action = "BALANCE_CHANGE",
            details = "Previous: $previousBalance, New: $newBalance, Change: $change"
        )
    }

    @Transactional
    fun logError(
        user: User,
        action: String,
        errorMessage: String
    ): AuditLog {
        return logUserAction(
            user = user,
            action = action,
            details = errorMessage,
            status = "ERROR"
        )
    }
} 