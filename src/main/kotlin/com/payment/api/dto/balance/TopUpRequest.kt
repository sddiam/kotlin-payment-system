package com.payment.api.dto.balance

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class TopUpRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "1000", message = "Minimum top-up amount is 1,000")
    val amount: BigDecimal
) 