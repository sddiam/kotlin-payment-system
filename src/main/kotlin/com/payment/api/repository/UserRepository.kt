package com.payment.api.repository

import com.payment.api.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
    fun findByIdAndIsActiveTrue(id: Long): Optional<User>
    fun findByEmailAndIsActiveTrue(email: String): User?
} 