package com.payment.api.interceptor

import io.github.bucket4j.Bucket
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class RateLimitInterceptor(
    private val ipRateLimitBucket: Bucket,
    private val userRateLimitBucket: Bucket
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ipAddress = request.remoteAddr
        val userId = request.getHeader("X-User-ID")

        // IP 기반 Rate Limiting
        if (!ipRateLimitBucket.tryConsume(1)) {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write("Rate limit exceeded for IP: $ipAddress")
            return false
        }

        // 사용자 기반 Rate Limiting (인증된 사용자의 경우)
        if (userId != null && !userRateLimitBucket.tryConsume(1)) {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.writer.write("Rate limit exceeded for user: $userId")
            return false
        }

        return true
    }
} 