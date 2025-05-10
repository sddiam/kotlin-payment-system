package com.payment.api.repository

import com.payment.api.entity.Transaction
import com.payment.api.entity.TransactionType
import com.payment.api.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime
import org.junit.jupiter.api.Assertions.*

@DataJpaTest
@ActiveProfiles("test")
class TransactionRepositoryTest {
    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @Test
    fun `findByUserId should return transactions for user`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val transaction1 = Transaction(
            user = user,
            amount = BigDecimal("1000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment 1"
        )
        val transaction2 = Transaction(
            user = user,
            amount = BigDecimal("2000"),
            type = TransactionType.TOP_UP,
            status = "COMPLETED",
            description = "Test top-up 1"
        )
        entityManager.persist(transaction1)
        entityManager.persist(transaction2)
        entityManager.flush()

        // When
        val transactions = transactionRepository.findByUserId(user.id!!)

        // Then
        assertEquals(2, transactions.size)
        assertTrue(transactions.any { it.amount == BigDecimal("1000") && it.type == TransactionType.PAYMENT })
        assertTrue(transactions.any { it.amount == BigDecimal("2000") && it.type == TransactionType.TOP_UP })
    }

    @Test
    fun `findByUserIdAndType should return transactions of specific type`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val transaction1 = Transaction(
            user = user,
            amount = BigDecimal("1000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment 1"
        )
        val transaction2 = Transaction(
            user = user,
            amount = BigDecimal("2000"),
            type = TransactionType.TOP_UP,
            status = "COMPLETED",
            description = "Test top-up 1"
        )
        entityManager.persist(transaction1)
        entityManager.persist(transaction2)
        entityManager.flush()

        // When
        val payments = transactionRepository.findByUserIdAndType(user.id!!, TransactionType.PAYMENT)

        // Then
        assertEquals(1, payments.size)
        assertEquals(BigDecimal("1000"), payments[0].amount)
        assertEquals(TransactionType.PAYMENT, payments[0].type)
    }

    @Test
    fun `sumAmountByUserIdAndTypeAndTimestampBetween should return correct sum`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val now = LocalDateTime.now()
        val transaction1 = Transaction(
            user = user,
            amount = BigDecimal("1000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment 1",
            timestamp = now
        )
        val transaction2 = Transaction(
            user = user,
            amount = BigDecimal("2000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment 2",
            timestamp = now.plusHours(1)
        )
        entityManager.persist(transaction1)
        entityManager.persist(transaction2)
        entityManager.flush()

        // When
        val sum = transactionRepository.sumAmountByUserIdAndTypeAndTimestampBetween(
            userId = user.id!!,
            type = TransactionType.PAYMENT,
            startDate = now.minusHours(1),
            endDate = now.plusHours(2)
        )

        // Then
        assertEquals(BigDecimal("3000"), sum)
    }

    @Test
    fun `findByUserIdAndTimestampBetween should return transactions in date range`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val now = LocalDateTime.now()
        val transaction1 = Transaction(
            user = user,
            amount = BigDecimal("1000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment 1",
            timestamp = now
        )
        val transaction2 = Transaction(
            user = user,
            amount = BigDecimal("2000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment 2",
            timestamp = now.plusDays(1)
        )
        entityManager.persist(transaction1)
        entityManager.persist(transaction2)
        entityManager.flush()

        // When
        val transactions = transactionRepository.findByUserIdAndTimestampBetween(
            userId = user.id!!,
            startDate = now.minusHours(1),
            endDate = now.plusHours(1)
        )

        // Then
        assertEquals(1, transactions.size)
        assertEquals(BigDecimal("1000"), transactions[0].amount)
    }

    @Test
    fun `save should persist transaction successfully`() {
        // Given
        val user = User(
            email = "test@example.com",
            password = "password123",
            name = "Test User",
            phoneNumber = "010-1234-5678"
        )
        entityManager.persist(user)

        val transaction = Transaction(
            user = user,
            amount = BigDecimal("1000"),
            type = TransactionType.PAYMENT,
            status = "COMPLETED",
            description = "Test payment"
        )

        // When
        val saved = transactionRepository.save(transaction)

        // Then
        assertNotNull(saved.id)
        val found = entityManager.find(Transaction::class.java, saved.id)
        assertNotNull(found)
        assertEquals(user.id, found.user.id)
        assertEquals(BigDecimal("1000"), found.amount)
        assertEquals(TransactionType.PAYMENT, found.type)
        assertEquals("COMPLETED", found.status)
    }
} 