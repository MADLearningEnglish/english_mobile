package com.mit.learning_english.presentation.feature.searchbook

sealed class SearchBookEvent {
    data class NavigateToBookDetail(val bookId: Int) : SearchBookEvent()
}
