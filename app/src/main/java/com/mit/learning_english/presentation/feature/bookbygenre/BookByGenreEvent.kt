package com.mit.learning_english.presentation.feature.bookbygenre

sealed class BookByGenreEvent {
    data class NavigateToBookDetail(val bookId: Int) : BookByGenreEvent()
}
