package com.payment.api.dto.transaction

import com.payment.api.entity.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse(
    val id: Long,
    val userId: Long,
    val amount: BigDecimal,
    val type: TransactionType,
    val status: String,
    val description: String?,
    val timestamp: LocalDateTime
) 