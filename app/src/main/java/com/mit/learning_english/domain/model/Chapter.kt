package com.mit.learning_english.domain.model

data class Chapter(
    val id: Int,
    val bookId: Int,
    val title: String,
    val description: String,
    val number: Int=0,
    val totalPages: Int=0,
    val totalDuration: Int=0
)