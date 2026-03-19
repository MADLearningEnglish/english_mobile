package com.mit.learning_english.domain.model

data class Chapter(
    val id: Int,
    val bookId: Int,
    val title: String,
    val description: String,
    val number: Int,
    val totalPages: Int,
    val totalDuration: Int
)