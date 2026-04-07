package com.mit.learning_english.presentation.feature.detailauthor

sealed class DetailAuthorEvent {
    data class NavigateToBookDetail(val bookId: Int) : DetailAuthorEvent()
}