package com.payment.api.repository

import com.payment.api.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.util.*
import org.junit.jupiter.api.Assertions.*

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `findByEmail should return user when email exists`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)
        entityManager.flush()

        // When
        val found = userRepository.findByEmail(user.email)

        // Then
        assertNotNull(found)
        assertEquals(user.email, found?.email)
        assertEquals(user.name, found?.name)
        assertEquals(user.phoneNumber, found?.phoneNumber)
    }

    @Test
    fun `findByEmail should return null when email does not exist`() {
        // Given
        val nonExistentEmail = "nonexistent@example.com"

        // When
        val found = userRepository.findByEmail(nonExistentEmail)

        // Then
        assertNull(found)
    }

    @Test
    fun `existsByEmail should return true when email exists`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)
        entityManager.flush()

        // When
        val exists = userRepository.existsByEmail(user.email)

        // Then
        assertTrue(exists)
    }

    @Test
    fun `existsByEmail should return false when email does not exist`() {
        // Given
        val nonExistentEmail = "nonexistent@example.com"

        // When
        val exists = userRepository.existsByEmail(nonExistentEmail)

        // Then
        assertFalse(exists)
    }

    @Test
    fun `save should persist user successfully`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )

        // When
        val saved = userRepository.save(user)

        // Then
        assertNotNull(saved.id)
        val found = entityManager.find(User::class.java, saved.id)
        assertNotNull(found)
        assertEquals(user.email, found.email)
        assertEquals(user.name, found.name)
        assertEquals(user.phoneNumber, found.phoneNumber)
    }

    @Test
    fun `findById should return user when id exists`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)
        entityManager.flush()

        // When
        val found = userRepository.findById(user.id!!)

        // Then
        assertTrue(found.isPresent)
        assertEquals(user.email, found.get().email)
        assertEquals(user.name, found.get().name)
        assertEquals(user.phoneNumber, found.get().phoneNumber)
    }

    @Test
    fun `findById should return empty when id does not exist`() {
        // Given
        val nonExistentId = 999L

        // When
        val found = userRepository.findById(nonExistentId)

        // Then
        assertFalse(found.isPresent)
    }
} 