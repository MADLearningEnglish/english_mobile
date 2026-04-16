package com.mit.learning_english.presentation.feature.historyreadbook

sealed class HistoryReadBookEvent {
    data class NavigateToBookDetail(val bookId: Int) : HistoryReadBookEvent()
}