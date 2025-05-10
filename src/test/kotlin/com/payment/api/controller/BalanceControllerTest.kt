package com.payment.api.controller

import com.payment.api.dto.balance.BalanceResponse
import com.payment.api.dto.balance.TopUpRequest
import com.payment.api.service.BalanceService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import com.fasterxml.jackson.databind.ObjectMapper
import java.math.BigDecimal

class BalanceControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var balanceService: BalanceService
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        balanceService = mockk()
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders
            .standaloneSetup(BalanceController(balanceService))
            .build()
    }

    @Test
    fun `getBalance should return 200 when balance exists`() {
        // Given
        val userId = 1L
        val response = BalanceResponse(
            userId = userId,
            amount = BigDecimal("10000"),
            lastUpdated = any()
        )

        coEvery { balanceService.getBalance(userId) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/users/$userId/balance"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.amount").value(response.amount))
            .andExpect(jsonPath("$.lastUpdated").exists())

        coVerify { balanceService.getBalance(userId) }
    }

    @Test
    fun `topUp should return 200 when top-up is successful`() {
        // Given
        val userId = 1L
        val request = TopUpRequest(amount = BigDecimal("5000"))
        val response = BalanceResponse(
            userId = userId,
            amount = BigDecimal("15000"),
            lastUpdated = any()
        )

        coEvery { balanceService.topUp(userId, request.amount) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/users/$userId/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.amount").value(response.amount))
            .andExpect(jsonPath("$.lastUpdated").exists())

        coVerify { balanceService.topUp(userId, request.amount) }
    }

    @Test
    fun `getBalance should return 404 when user does not exist`() {
        // Given
        val userId = 999L

        coEvery { balanceService.getBalance(userId) } throws NoSuchElementException("User not found")

        // When & Then
        mockMvc.perform(get("/api/v1/users/$userId/balance"))
            .andExpect(status().isNotFound)

        coVerify { balanceService.getBalance(userId) }
    }

    @Test
    fun `topUp should return 404 when user does not exist`() {
        // Given
        val userId = 999L
        val request = TopUpRequest(amount = BigDecimal("5000"))

        coEvery { balanceService.topUp(userId, request.amount) } throws NoSuchElementException("User not found")

        // When & Then
        mockMvc.perform(
            post("/api/v1/users/$userId/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)

        coVerify { balanceService.topUp(userId, request.amount) }
    }

    @Test
    fun `topUp should return 400 when amount is invalid`() {
        // Given
        val userId = 1L
        val request = TopUpRequest(amount = BigDecimal("-1000"))

        // When & Then
        mockMvc.perform(
            post("/api/v1/users/$userId/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        coVerify(exactly = 0) { balanceService.topUp(any(), any()) }
    }

    @Test
    fun `topUp should return 400 when amount exceeds daily limit`() {
        // Given
        val userId = 1L
        val request = TopUpRequest(amount = BigDecimal("10000000"))

        coEvery { balanceService.topUp(userId, request.amount) } throws IllegalArgumentException("Daily limit exceeded")

        // When & Then
        mockMvc.perform(
            post("/api/v1/users/$userId/topup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        coVerify { balanceService.topUp(userId, request.amount) }
    }
} 