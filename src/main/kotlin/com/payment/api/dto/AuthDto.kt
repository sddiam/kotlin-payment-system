package com.payment.api.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val type: String = "Bearer"
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val phoneNumber: String
) 