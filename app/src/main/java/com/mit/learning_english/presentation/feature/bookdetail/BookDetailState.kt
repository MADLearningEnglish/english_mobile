package com.mit.learning_english.presentation.feature.bookdetail

import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.presentation.base.BaseUiState

data class BookDetailState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val book: BookDetail? = null
) : BaseUiState<BookDetailState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): BookDetailState {
        return copy(
            isLoading = isLoading ?: this.isLoading,
            errorMessage = errorMessage ?: this.errorMessage
        )
    }

    fun copyWith(
        isLoading: Boolean?, errorMessage: String?, book: BookDetail?
    ) = copy(
        isLoading = isLoading ?: this.isLoading,
        errorMessage = errorMessage ?: this.errorMessage,
        book = book ?: this.book
    )
}
