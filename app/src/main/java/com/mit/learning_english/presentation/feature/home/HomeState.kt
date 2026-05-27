package com.mit.learning_english.presentation.feature.home

import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.Genre

/**
 * Đại diện cho trạng thái giao diện (UI State) của màn hình chính (HomeFragment).
 *
 * @property recommendBooks Danh sách các cuốn sách được đề xuất hiển thị ở màn hình chính.
 * @property isRecommendBooksLoading Cờ trạng thái đang tải danh sách sách đề xuất.
 * @property genres Danh sách các thể loại sách hiện có.
 * @property isGenresLoading Cờ trạng thái đang tải danh sách thể loại sách.
 */
data class HomeState(
    val recommendBooks: List<Book> = emptyList(),
    val isRecommendBooksLoading: Boolean = false,
    val genres: List<Genre> = emptyList(),
    val isGenresLoading: Boolean = false
)
