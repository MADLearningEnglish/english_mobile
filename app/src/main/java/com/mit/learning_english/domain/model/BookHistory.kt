package com.mit.learning_english.domain.model

import java.time.LocalDateTime

data class BookHistory(
    val id: Int,
    val title: String,
    val language: String,
    val coverUrl: String,
    val genresName: String,
    val authorsName: String,
    val processPercent: Double,
    val lastRead: LocalDateTime,
    val isFavorite: Boolean
)