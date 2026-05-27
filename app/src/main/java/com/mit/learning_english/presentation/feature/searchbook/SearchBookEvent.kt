package com.mit.learning_english.presentation.feature.searchbook

/**
 * Các sự kiện điều hướng hoặc hành vi tương tác từ màn hình Tìm kiếm sách.
 */
sealed class SearchBookEvent {
    /**
     * Điều hướng sang màn hình Chi tiết sách (BookDetailFragment).
     */
    data class NavigateToBookDetail(val bookId: Int) : SearchBookEvent()
}
