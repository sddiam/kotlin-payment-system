package com.payment.api.controller

import com.payment.api.dto.payment.PaymentRequest
import com.payment.api.dto.payment.PaymentResponse
import com.payment.api.service.PaymentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/payments")
class PaymentController(
    private val paymentService: PaymentService
) {
    @PostMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @ResponseStatus(HttpStatus.CREATED)
    fun processPayment(
        @PathVariable userId: Long,
        @Valid @RequestBody request: PaymentRequest
    ): PaymentResponse {
        return paymentService.processPayment(userId, request)
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN') or @paymentService.isPaymentOwner(#paymentId, authentication.principal.id)")
    fun getPaymentStatus(@PathVariable paymentId: Long): PaymentResponse {
        return paymentService.getPaymentStatus(paymentId)
    }
} 