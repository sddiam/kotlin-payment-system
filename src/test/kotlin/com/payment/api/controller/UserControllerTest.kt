package com.payment.api.controller

import com.payment.api.dto.user.UserCreateRequest
import com.payment.api.dto.user.UserResponse
import com.payment.api.dto.user.UserUpdateRequest
import com.payment.api.service.UserService
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.HttpStatus

class UserControllerTest {
    private lateinit var mockMvc: MockMvc
    private lateinit var userService: UserService
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        userService = mockk()
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders
            .standaloneSetup(UserController(userService))
            .build()
    }

    @Test
    fun `createUser should return 201 when user is created successfully`() {
        // Given
        val request = UserCreateRequest(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        val response = UserResponse(
            id = 1L,
            email = request.email,
            name = request.name,
            phoneNumber = request.phoneNumber
        )

        coEvery { userService.createUser(any()) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.phoneNumber").value(response.phoneNumber))

        coVerify { userService.createUser(any()) }
    }

    @Test
    fun `getUser should return 200 when user exists`() {
        // Given
        val userId = 1L
        val response = UserResponse(
            id = userId,
            email = "test@example.com",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )

        coEvery { userService.getUser(userId) } returns response

        // When & Then
        mockMvc.perform(get("/api/v1/users/$userId"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.email").value(response.email))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.phoneNumber").value(response.phoneNumber))

        coVerify { userService.getUser(userId) }
    }

    @Test
    fun `updateUser should return 200 when user is updated successfully`() {
        // Given
        val userId = 1L
        val request = UserUpdateRequest(
            name = "Updated User",
            phoneNumber = "010-8765-4321"
        )
        val response = UserResponse(
            id = userId,
            email = "test@example.com",
            name = request.name,
            phoneNumber = request.phoneNumber
        )

        coEvery { userService.updateUser(userId, any()) } returns response

        // When & Then
        mockMvc.perform(
            put("/api/v1/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(response.id))
            .andExpect(jsonPath("$.name").value(response.name))
            .andExpect(jsonPath("$.phoneNumber").value(response.phoneNumber))

        coVerify { userService.updateUser(userId, any()) }
    }

    @Test
    fun `createUser should return 400 when request is invalid`() {
        // Given
        val request = UserCreateRequest(
            email = "invalid-email",
            password = "",
            name = "",
            phoneNumber = "invalid-phone"
        )

        // When & Then
        mockMvc.perform(
            post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)

        coVerify(exactly = 0) { userService.createUser(any()) }
    }

    @Test
    fun `getUser should return 404 when user does not exist`() {
        // Given
        val userId = 999L

        coEvery { userService.getUser(userId) } throws NoSuchElementException("User not found")

        // When & Then
        mockMvc.perform(get("/api/v1/users/$userId"))
            .andExpect(status().isNotFound)

        coVerify { userService.getUser(userId) }
    }

    @Test
    fun `updateUser should return 404 when user does not exist`() {
        // Given
        val userId = 999L
        val request = UserUpdateRequest(
            name = "Updated User",
            phoneNumber = "010-8765-4321"
        )

        coEvery { userService.updateUser(userId, any()) } throws NoSuchElementException("User not found")

        // When & Then
        mockMvc.perform(
            put("/api/v1/users/$userId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)

        coVerify { userService.updateUser(userId, any()) }
    }
} 