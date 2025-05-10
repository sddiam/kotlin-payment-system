package com.payment.api.service

import com.payment.api.dto.user.UserCreateRequest
import com.payment.api.dto.user.UserResponse
import com.payment.api.entity.User
import com.payment.api.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @Transactional
    fun createUser(request: UserCreateRequest): UserResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            name = request.name,
            phoneNumber = request.phoneNumber
        )

        val savedUser = userRepository.save(user)
        return savedUser.toResponse()
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with id: $id") }
        return user.toResponse()
    }

    @Transactional(readOnly = true)
    fun getUserByEmail(email: String): UserResponse {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found with email: $email")
        return user.toResponse()
    }

    @Transactional
    fun updateUser(id: Long, request: UserCreateRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { IllegalArgumentException("User not found with id: $id") }

        if (request.email != user.email && userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        user.apply {
            email = request.email
            password = passwordEncoder.encode(request.password)
            name = request.name
            phoneNumber = request.phoneNumber
        }

        val updatedUser = userRepository.save(user)
        return updatedUser.toResponse()
    }

    @Transactional
    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw IllegalArgumentException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }

    private fun User.toResponse() = UserResponse(
        id = id!!,
        email = email,
        name = name,
        phoneNumber = phoneNumber,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 