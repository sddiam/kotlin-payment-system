package com.payment.api.repository

import com.payment.api.entity.AuditLog
import com.payment.api.entity.AuditLogType
import com.payment.api.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.*

@DataJpaTest
@ActiveProfiles("test")
class AuditLogRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var auditLogRepository: AuditLogRepository

    @Test
    fun `findByUserId should return audit logs for user`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val log1 = AuditLog(
            user = user,
            action = "LOGIN",
            type = AuditLogType.USER_ACTION,
            status = "SUCCESS",
            details = "User logged in successfully"
        )
        val log2 = AuditLog(
            user = user,
            action = "PAYMENT",
            type = AuditLogType.TRANSACTION,
            status = "SUCCESS",
            details = "Payment processed successfully"
        )
        entityManager.persist(log1)
        entityManager.persist(log2)
        entityManager.flush()

        // When
        val logs = auditLogRepository.findByUserId(user.id!!)

        // Then
        assertEquals(2, logs.size)
        assertTrue(logs.any { it.action == "LOGIN" && it.type == AuditLogType.USER_ACTION })
        assertTrue(logs.any { it.action == "PAYMENT" && it.type == AuditLogType.TRANSACTION })
    }

    @Test
    fun `findByType should return audit logs of specific type`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val log1 = AuditLog(
            user = user,
            action = "LOGIN",
            type = AuditLogType.USER_ACTION,
            status = "SUCCESS",
            details = "User logged in successfully"
        )
        val log2 = AuditLog(
            user = user,
            action = "PAYMENT",
            type = AuditLogType.TRANSACTION,
            status = "SUCCESS",
            details = "Payment processed successfully"
        )
        entityManager.persist(log1)
        entityManager.persist(log2)
        entityManager.flush()

        // When
        val userActionLogs = auditLogRepository.findByType(AuditLogType.USER_ACTION)

        // Then
        assertEquals(1, userActionLogs.size)
        assertEquals("LOGIN", userActionLogs[0].action)
        assertEquals(AuditLogType.USER_ACTION, userActionLogs[0].type)
    }

    @Test
    fun `findByTimestampBetween should return audit logs in date range`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val now = LocalDateTime.now()
        val log1 = AuditLog(
            user = user,
            action = "LOGIN",
            type = AuditLogType.USER_ACTION,
            status = "SUCCESS",
            details = "User logged in successfully",
            timestamp = now
        )
        val log2 = AuditLog(
            user = user,
            action = "PAYMENT",
            type = AuditLogType.TRANSACTION,
            status = "SUCCESS",
            details = "Payment processed successfully",
            timestamp = now.plusDays(1)
        )
        entityManager.persist(log1)
        entityManager.persist(log2)
        entityManager.flush()

        // When
        val logs = auditLogRepository.findByTimestampBetween(
            startDate = now.minusHours(1),
            endDate = now.plusHours(1)
        )

        // Then
        assertEquals(1, logs.size)
        assertEquals("LOGIN", logs[0].action)
    }

    @Test
    fun `save should persist audit log successfully`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val log = AuditLog(
            user = user,
            action = "LOGIN",
            type = AuditLogType.USER_ACTION,
            status = "SUCCESS",
            details = "User logged in successfully"
        )

        // When
        val saved = auditLogRepository.save(log)

        // Then
        assertNotNull(saved.id)
        val found = entityManager.find(AuditLog::class.java, saved.id)
        assertNotNull(found)
        assertEquals(user.id, found.user.id)
        assertEquals("LOGIN", found.action)
        assertEquals(AuditLogType.USER_ACTION, found.type)
        assertEquals("SUCCESS", found.status)
    }
} 