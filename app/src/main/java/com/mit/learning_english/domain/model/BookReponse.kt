package com.mit.learning_english.domain.model

import java.time.LocalDateTime

data class BookReponse(
    val id: Int,
    val title: String,
    val language: String,
    val coverUrl: String,
    val genresName: String,
    val authorsName: String,
    val processPercent: Double,
    val pageLastRead: Int,
    val lastRead: LocalDateTime,
    val isFavorite: Boolean
)