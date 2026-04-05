package com.mit.learning_english.data.remote.dto

data class PaginatedResponse<T>(
    val page: Int,
    val limit: Int,
    val total: Int,
    val data: List<T>
)
