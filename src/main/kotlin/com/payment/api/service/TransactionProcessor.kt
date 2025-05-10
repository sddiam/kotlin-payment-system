package com.payment.api.service

import com.payment.api.entity.Balance
import com.payment.api.entity.Transaction
import com.payment.api.entity.TransactionType
import com.payment.api.repository.BalanceRepository
import com.payment.api.repository.TransactionRepository
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class TransactionProcessor(
    private val balanceRepository: BalanceRepository,
    private val transactionRepository: TransactionRepository
) {
    @Transactional
    fun processWithOptimisticLock(
        userId: Long,
        amount: BigDecimal,
        type: TransactionType,
        description: String?
    ): Transaction {
        val balance = balanceRepository.findByUserIdWithLock(userId, LockModeType.OPTIMISTIC)
            ?: throw IllegalArgumentException("Balance not found for user: $userId")

        when (type) {
            TransactionType.PAYMENT -> {
                if (balance.amount < amount) {
                    throw IllegalArgumentException("Insufficient balance")
                }
                balance.amount = balance.amount.subtract(amount)
            }
            TransactionType.TOP_UP -> {
                balance.amount = balance.amount.add(amount)
            }
        }

        balance.lastUpdated = LocalDateTime.now()
        balanceRepository.save(balance)

        val transaction = Transaction(
            user = balance.user,
            amount = amount,
            type = type,
            status = "COMPLETED",
            description = description,
            timestamp = LocalDateTime.now()
        )

        return transactionRepository.save(transaction)
    }

    @Transactional
    fun processWithPessimisticLock(
        userId: Long,
        amount: BigDecimal,
        type: TransactionType,
        description: String?
    ): Transaction {
        val balance = balanceRepository.findByUserIdWithLock(userId, LockModeType.PESSIMISTIC_WRITE)
            ?: throw IllegalArgumentException("Balance not found for user: $userId")

        when (type) {
            TransactionType.PAYMENT -> {
                if (balance.amount < amount) {
                    throw IllegalArgumentException("Insufficient balance")
                }
                balance.amount = balance.amount.subtract(amount)
            }
            TransactionType.TOP_UP -> {
                balance.amount = balance.amount.add(amount)
            }
        }

        balance.lastUpdated = LocalDateTime.now()
        balanceRepository.save(balance)

        val transaction = Transaction(
            user = balance.user,
            amount = amount,
            type = type,
            status = "COMPLETED",
            description = description,
            timestamp = LocalDateTime.now()
        )

        return transactionRepository.save(transaction)
    }

    @Transactional
    fun validateAndProcessTransaction(
        userId: Long,
        amount: BigDecimal,
        type: TransactionType,
        description: String?,
        useOptimisticLock: Boolean = true
    ): Transaction {
        // Validate transaction amount
        validateTransactionAmount(amount, type)

        // Process transaction with selected locking strategy
        return if (useOptimisticLock) {
            processWithOptimisticLock(userId, amount, type, description)
        } else {
            processWithPessimisticLock(userId, amount, type, description)
        }
    }

    private fun validateTransactionAmount(amount: BigDecimal, type: TransactionType) {
        when (type) {
            TransactionType.PAYMENT -> {
                if (amount < BigDecimal("100")) {
                    throw IllegalArgumentException("Minimum payment amount is 100")
                }
                if (amount > BigDecimal("1000000")) {
                    throw IllegalArgumentException("Maximum payment amount is 1,000,000")
                }
            }
            TransactionType.TOP_UP -> {
                if (amount < BigDecimal("1000")) {
                    throw IllegalArgumentException("Minimum top-up amount is 1,000")
                }
                if (amount > BigDecimal("10000000")) {
                    throw IllegalArgumentException("Maximum top-up amount is 10,000,000")
                }
            }
        }
    }
} 