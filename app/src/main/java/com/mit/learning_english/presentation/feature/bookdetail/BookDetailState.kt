package com.mit.learning_english.presentation.feature.bookdetail

import com.mit.learning_english.domain.model.Chapter

data class BookDetailState(
    val id: Int = 0,
    val title: String = "",
    val language: String = "",
    val coverUrl: String = "",
    val genresName: String = "",
    val authorsName: String = "",
    val progressPercent: Double = 0.0,
    val chapters: List<Chapter> = emptyList(),
    val lastReadNumberPage: Int = 0,
    val isFavorite: Boolean = false,
)
