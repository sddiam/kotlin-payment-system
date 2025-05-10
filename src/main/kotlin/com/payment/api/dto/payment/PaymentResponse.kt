package com.payment.api.dto.payment

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponse(
    val id: Long,
    val userId: Long,
    val amount: BigDecimal,
    val description: String,
    val status: String,
    val timestamp: LocalDateTime
) 