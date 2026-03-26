package com.mit.learning_english.presentation.feature.home

import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.Genre

data class HomeState(
    val recommendBooks: List<Book> = emptyList(),
    val isRecommendBooksLoading: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val isGenresLoading: Boolean = false
)
