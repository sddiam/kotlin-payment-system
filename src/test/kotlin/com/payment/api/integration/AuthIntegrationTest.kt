package com.payment.api.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.payment.api.dto.LoginRequest
import com.payment.api.dto.RegisterRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `register should create new user and return 201`() {
        val request = RegisterRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.email").value(request.email))
            .andExpect(jsonPath("$.name").value(request.name))
    }

    @Test
    fun `login should return JWT token`() {
        // First register a user
        val registerRequest = RegisterRequest(
            email = "login@example.com",
            password = "password123",
            name = "Login Test",
            phoneNumber = "010-1234-5678"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
        )

        // Then try to login
        val loginRequest = LoginRequest(
            email = registerRequest.email,
            password = registerRequest.password
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.type").value("Bearer"))
    }

    @Test
    fun `register should return 400 for invalid input`() {
        val request = RegisterRequest(
            email = "invalid-email",
            password = "short",
            name = "",
            phoneNumber = "invalid-phone"
        )

        mockMvc.perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Validation Error"))
    }

    @Test
    fun `login should return 401 for invalid credentials`() {
        val loginRequest = LoginRequest(
            email = "nonexistent@example.com",
            password = "wrongpassword"
        )

        mockMvc.perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.error").value("Authentication Error"))
    }
} 