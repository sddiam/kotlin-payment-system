package com.payment.api.dto.transaction

import org.springframework.data.domain.Page

data class TransactionPageResponse(
    val transactions: List<TransactionResponse>,
    val currentPage: Int,
    val totalPages: Int,
    val totalElements: Long,
    val pageSize: Int
) {
    companion object {
        fun from(page: Page<TransactionResponse>): TransactionPageResponse {
            return TransactionPageResponse(
                transactions = page.content,
                currentPage = page.number,
                totalPages = page.totalPages,
                totalElements = page.totalElements,
                pageSize = page.size
            )
        }
    }
} 