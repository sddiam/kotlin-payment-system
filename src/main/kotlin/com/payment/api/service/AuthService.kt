package com.payment.api.service

import com.payment.api.dto.LoginRequest
import com.payment.api.dto.LoginResponse
import com.payment.api.dto.RegisterRequest
import com.payment.api.entity.User
import com.payment.api.repository.UserRepository
import com.payment.api.security.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider
) {
    @Transactional
    fun register(request: RegisterRequest): User {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            phoneNumber = request.phoneNumber
        )

        return userRepository.save(user)
    }

    fun login(request: LoginRequest): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.email, request.password)
        )

        SecurityContextHolder.getContext().authentication = authentication
        val token = jwtTokenProvider.generateToken(authentication)

        return LoginResponse(token = token)
    }
} 