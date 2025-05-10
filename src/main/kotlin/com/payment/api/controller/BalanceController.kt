package com.payment.api.controller

import com.payment.api.dto.balance.BalanceResponse
import com.payment.api.dto.balance.TopUpRequest
import com.payment.api.service.BalanceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users/{userId}/balance")
class BalanceController(
    private val balanceService: BalanceService
) {
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    fun getBalance(@PathVariable userId: Long): BalanceResponse {
        return balanceService.getBalance(userId)
    }

    @PostMapping("/topup")
    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    @ResponseStatus(HttpStatus.OK)
    fun topUp(
        @PathVariable userId: Long,
        @Valid @RequestBody request: TopUpRequest
    ): BalanceResponse {
        return balanceService.topUp(userId, request)
    }
} 