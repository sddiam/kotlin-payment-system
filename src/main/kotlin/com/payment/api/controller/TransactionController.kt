package com.payment.api.controller

import com.payment.api.dto.transaction.TransactionPageResponse
import com.payment.api.entity.TransactionType
import com.payment.api.service.TransactionService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/users/{userId}/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    fun getTransactionHistory(
        @PathVariable userId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        @RequestParam(required = false) type: TransactionType?
    ): TransactionPageResponse {
        return transactionService.getTransactionHistory(
            userId = userId,
            page = page,
            size = size,
            startDate = startDate,
            endDate = endDate,
            type = type
        )
    }
} 