package com.payment.api.controller

import com.payment.api.dto.payment.PaymentRequest
import com.payment.api.dto.payment.PaymentResponse
import com.payment.api.service.PaymentService
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

class PaymentControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var paymentService: PaymentService
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        paymentService = mockk()
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders
            .standaloneSetup(PaymentController(paymentService))
            .build()
    }

    @Test
    fun `processPayment should return 200 when payment is successful`() {
        // Given
        val request = PaymentRequest(
            userId = 1L,
            amount = BigDecimal("5000"),
            description = "Test payment"
        )
        val response = PaymentResponse(
            id = 1L,
            userId = request.userId,
            amount = request.amount,
            status = "COMPLETED",
            description = request.description,
            timestamp = any()
        )

        coEvery { paymentService.processPayment(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.amount").value(response.amount))
            .andExpect(jsonPath("$.status").value(response.status))
            .andExpect(jsonPath("$.description").value(response.description))
            .andExpect(jsonPath("$.timestamp").exists())

        coVerify { paymentService.processPayment(any()) }
    }

    @Test
    fun `getPayment should return 200 when payment exists`() {
        // Given
        val paymentId = 1L
        val response = PaymentResponse(
            id = paymentId,
            userId = 1L,
            amount = BigDecimal("5000"),
            status = "COMPLETED",
            description = "Test payment",
            timestamp = any()
        )

        coEvery { paymentService.getPayment(paymentId) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/payments/$paymentId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.userId").value(response.userId))
            .andExpect(jsonPath("$.amount").value(response.amount))
            .andExpect(jsonPath("$.status").value(response.status))
            .andExpect(jsonPath("$.description").value(response.description))
            .andExpect(jsonPath("$.timestamp").exists())

        coVerify { paymentService.getPayment(paymentId) }
    }

    @Test
    fun `processPayment should return 400 when request is invalid`() {
        // Given
        val request = PaymentRequest(
            userId = 1L,
            amount = BigDecimal("-1000"),
            description = ""
        )

        // When & Then
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        coVerify(exactly = 0) { paymentService.processPayment(any()) }
    }

    @Test
    fun `processPayment should return 404 when user does not exist`() {
        // Given
        val request = PaymentRequest(
            userId = 999L,
            amount = BigDecimal("5000"),
            description = "Test payment"
        )

        coEvery { paymentService.processPayment(any()) } throws NoSuchElementException("User not found")

        // When & Then
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)

        coVerify { paymentService.processPayment(any()) }
    }

    @Test
    fun `processPayment should return 400 when insufficient balance`() {
        // Given
        val request = PaymentRequest(
            userId = 1L,
            amount = BigDecimal("1000000"),
            description = "Test payment"
        )

        coEvery { paymentService.processPayment(any()) } throws IllegalArgumentException("Insufficient balance")

        // When & Then
        mockMvc.perform(
            post("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        coVerify { paymentService.processPayment(any()) }
    }

    @Test
    fun `getPayment should return 404 when payment does not exist`() {
        // Given
        val paymentId = 999L

        coEvery { paymentService.getPayment(paymentId) } throws NoSuchElementException("Payment not found")

        // When & Then
        mockMvc.perform(get("/api/v1/payments/$paymentId"))
            .andExpect(status().isNotFound)

        coVerify { paymentService.getPayment(paymentId) }
    }
} 