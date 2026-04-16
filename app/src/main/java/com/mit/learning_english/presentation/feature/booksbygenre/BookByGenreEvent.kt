package com.mit.learning_english.presentation.feature.booksbygenre

sealed class BookByGenreEvent {
    data class NavigateToBookDetail(val bookId: Int) : BookByGenreEvent()
}
