package com.mit.learning_english.presentation.feature.recommendbook

/**
 * Các sự kiện điều hướng hành vi tương tác từ màn hình danh sách sách đề xuất.
 */
sealed class RecommendBookEvent {
    /**
     * Điều hướng sang màn hình Chi tiết sách (BookDetailFragment).
     */
    data class NavigateToBookDetail(val bookId: Int) : RecommendBookEvent()
}
