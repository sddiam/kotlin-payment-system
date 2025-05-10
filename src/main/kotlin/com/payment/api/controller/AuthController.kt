package com.payment.api.controller

import com.payment.api.dto.LoginRequest
import com.payment.api.dto.LoginResponse
import com.payment.api.dto.RegisterRequest
import com.payment.api.dto.UserResponse
import com.payment.api.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication management APIs")
class AuthController(
    private val authService: AuthService
) {
    @Operation(
        summary = "Register a new user",
        description = "Creates a new user account with the provided information"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "User successfully registered"),
            ApiResponse(responseCode = "400", description = "Invalid input data"),
            ApiResponse(responseCode = "409", description = "Email already exists")
        ]
    )
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val user = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.from(user))
    }

    @Operation(
        summary = "Login user",
        description = "Authenticates a user and returns a JWT token"
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "Login successful"),
            ApiResponse(responseCode = "401", description = "Invalid credentials")
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }
} 