package com.payment.api.service

import com.payment.api.dto.balance.BalanceResponse
import com.payment.api.dto.balance.TopUpRequest
import com.payment.api.entity.Balance
import com.payment.api.entity.Transaction
import com.payment.api.entity.TransactionType
import com.payment.api.repository.BalanceRepository
import com.payment.api.repository.TransactionRepository
import com.payment.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class BalanceService(
    private val balanceRepository: BalanceRepository,
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) {
    @Transactional(readOnly = true)
    fun getBalance(userId: Long): BalanceResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }
        
        val balance = balanceRepository.findByUser(user)
            ?: throw IllegalArgumentException("Balance not found for user: $userId")
        
        return balance.toResponse()
    }

    @Transactional
    fun topUp(userId: Long, request: TopUpRequest): BalanceResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }
        
        val balance = balanceRepository.findByUser(user)
            ?: Balance(user = user, amount = BigDecimal.ZERO)
        
        balance.amount = balance.amount.add(request.amount)
        balance.lastUpdated = LocalDateTime.now()
        
        val updatedBalance = balanceRepository.save(balance)
        
        // Create transaction record
        val transaction = Transaction(
            user = user,
            amount = request.amount,
            type = TransactionType.TOP_UP,
            status = "COMPLETED",
            timestamp = LocalDateTime.now()
        )
        transactionRepository.save(transaction)
        
        return updatedBalance.toResponse()
    }

    @Transactional
    fun deduct(userId: Long, amount: BigDecimal): BalanceResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }
        
        val balance = balanceRepository.findByUser(user)
            ?: throw IllegalArgumentException("Balance not found for user: $userId")
        
        if (balance.amount < amount) {
            throw IllegalArgumentException("Insufficient balance")
        }
        
        balance.amount = balance.amount.subtract(amount)
        balance.lastUpdated = LocalDateTime.now()
        
        val updatedBalance = balanceRepository.save(balance)
        
        // Create transaction record
        val transaction = Transaction(
            user = user,
            amount = amount,
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            timestamp = LocalDateTime.now()
        )
        transactionRepository.save(transaction)
        
        return updatedBalance.toResponse()
    }

    private fun Balance.toResponse() = BalanceResponse(
        userId = user.id!!,
        amount = amount,
        lastUpdated = lastUpdated
    )
} 