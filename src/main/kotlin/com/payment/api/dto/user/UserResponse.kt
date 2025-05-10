package com.payment.api.dto.user

import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val phoneNumber: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 