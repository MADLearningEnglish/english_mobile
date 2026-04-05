package com.mit.learning_english.presentation.feature.recommendbook

sealed class RecommendBookEvent {
    data class NavigateToBookDetail(val bookId: Int) : RecommendBookEvent()
}
