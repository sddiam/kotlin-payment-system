package com.payment.api.config

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class RateLimitConfig {

    @Bean
    fun ipRateLimitBucket(): Bucket {
        val refill = Refill.intervally(100, Duration.ofMinutes(1))
        val limit = Bandwidth.classic(100, refill)
        return Bucket.builder().addLimit(limit).build()
    }

    @Bean
    fun userRateLimitBucket(): Bucket {
        val refill = Refill.intervally(1000, Duration.ofMinutes(1))
        val limit = Bandwidth.classic(1000, refill)
        return Bucket.builder().addLimit(limit).build()
    }
} 