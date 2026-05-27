package com.mit.learning_english.presentation.feature.bookdetail

import com.mit.learning_english.presentation.feature.readbook.ReadBookArgs

/**
 * Các sự kiện điều hướng hoặc hành vi tương tác từ màn hình Chi tiết sách.
 */
sealed class BookDetailEvent {
    /**
     * Sự kiện điều hướng sang màn hình đọc sách (ReadBookFragment) kèm theo các đối số cấu hình.
     */
    data class NavigateToReadBook(val readBookArgs: ReadBookArgs) : BookDetailEvent()

}