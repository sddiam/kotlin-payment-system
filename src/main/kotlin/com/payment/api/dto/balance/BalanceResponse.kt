package com.payment.api.dto.balance

import java.math.BigDecimal
import java.time.LocalDateTime

data class BalanceResponse(
    val userId: Long,
    val amount: BigDecimal,
    val lastUpdated: LocalDateTime
) 