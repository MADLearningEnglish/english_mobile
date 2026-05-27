package com.mit.learning_english.presentation.feature.historyreadbook

/**
 * Các sự kiện điều hướng từ màn hình lịch sử đọc.
 */
sealed class HistoryReadBookEvent {
    /**
     * Điều hướng sang màn hình chi tiết sách.
     */
    data class NavigateToBookDetail(val bookId: Int) : HistoryReadBookEvent()
}