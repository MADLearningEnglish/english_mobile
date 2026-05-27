package com.mit.learning_english.presentation.feature.searchbook

/**
 * Trạng thái giao diện (UI State) của màn hình Tìm kiếm sách.
 *
 * @property searchQuery Từ khóa tìm kiếm hiện tại do người dùng nhập vào.
 */
data class SearchBookState(
    val searchQuery: String = ""
)
