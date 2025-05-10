package com.payment.api.dto.auth

data class AuthResponse(
    val token: String,
    val type: String = "Bearer"
) 