package com.payment.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PaymentSystemApplication

fun main(args: Array<String>) {
    runApplication<PaymentSystemApplication>(*args)
} 