package com.mit.learning_english.presentation.feature.home

import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.presentation.base.BaseUiState

data class HomeState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val recommendBooks: List<Book> = emptyList(),
    val isRecommendBooksLoading: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val isGenresLoading: Boolean = false
) : BaseUiState<HomeState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): HomeState {
        return copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }

    fun copyWith(
        isLoading: Boolean? = null,
        errorMessage: String? = null,
        recommendBooks: List<Book>? = null,
        isRecommendBooksLoading: Boolean? = null,
        genres: List<Genre>? = null,
        isGenresLoading: Boolean? = null
    ): HomeState {
        return copy(
            isLoading = isLoading ?: this.isLoading,
            errorMessage ?: this.errorMessage,
            recommendBooks = recommendBooks ?: this.recommendBooks,
            isRecommendBooksLoading = isRecommendBooksLoading ?: this.isRecommendBooksLoading,
            genres = genres ?: this.genres,
            isGenresLoading = isGenresLoading ?: this.isGenresLoading
        )
    }
}