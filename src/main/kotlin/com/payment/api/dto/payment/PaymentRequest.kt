package com.payment.api.dto.payment

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class PaymentRequest(
    @field:NotNull(message = "Amount is required")
    @field:DecimalMin(value = "100", message = "Minimum payment amount is 100")
    val amount: BigDecimal,

    @field:NotNull(message = "Description is required")
    val description: String
) 