package com.payment.api.service

import com.payment.api.dto.payment.PaymentRequest
import com.payment.api.dto.payment.PaymentResponse
import com.payment.api.entity.Transaction
import com.payment.api.entity.TransactionType
import com.payment.api.repository.TransactionRepository
import com.payment.api.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class PaymentService(
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository,
    private val balanceService: BalanceService
) {
    @Transactional
    fun processPayment(userId: Long, request: PaymentRequest): PaymentResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found with id: $userId") }

        // Deduct balance
        balanceService.deduct(userId, request.amount)

        // Create transaction record
        val transaction = Transaction(
            user = user,
            amount = request.amount,
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = request.description,
            timestamp = LocalDateTime.now()
        )
        val savedTransaction = transactionRepository.save(transaction)

        return savedTransaction.toPaymentResponse()
    }

    @Transactional(readOnly = true)
    fun getPaymentStatus(paymentId: Long): PaymentResponse {
        val transaction = transactionRepository.findById(paymentId)
            .orElseThrow { IllegalArgumentException("Payment not found with id: $paymentId") }

        if (transaction.type != TransactionType.PAYMENT) {
            throw IllegalArgumentException("Transaction is not a payment")
        }

        return transaction.toPaymentResponse()
    }

    private fun Transaction.toPaymentResponse() = PaymentResponse(
        id = id!!,
        userId = user.id!!,
        amount = amount,
        description = description ?: "",
        status = status,
        timestamp = timestamp
    )
} 