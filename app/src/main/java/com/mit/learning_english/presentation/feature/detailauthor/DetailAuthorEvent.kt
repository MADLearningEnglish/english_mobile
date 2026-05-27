package com.mit.learning_english.presentation.feature.detailauthor

/**
 * Các sự kiện điều hướng từ màn hình chi tiết tác giả.
 */
sealed class DetailAuthorEvent {
    /**
     * Điều hướng sang màn hình chi tiết sách theo `bookId`.
     */
    data class NavigateToBookDetail(val bookId: Int) : DetailAuthorEvent()
}