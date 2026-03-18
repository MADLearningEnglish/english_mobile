package com.mit.learning_english.domain.model

import java.time.LocalDateTime

data class BookProgress(
    val progressPercent: Double,
    val lastRead: LocalDateTime,
    val isFavorite: Boolean,
    val book: Book
)
