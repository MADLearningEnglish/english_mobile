package com.mit.learning_english.presentation.feature.detailauthor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.mit.learning_english.domain.usecase.book.GetBooksByAuthorPagingUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
/**
 * ViewModel quản lý dữ liệu tác giả và danh sách sách của tác giả.
 */
class DetailAuthorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getBooksByAuthorPagingUseCase: GetBooksByAuthorPagingUseCase
) : BaseViewModel<DetailAuthorState, DetailAuthorEvent>(DetailAuthorState()) {

    private val authorId: Int = savedStateHandle["authorId"] ?: 0

    /**
     * Luồng sách của tác giả theo cơ chế phân trang.
     */
    val authorBooks = getBooksByAuthorPagingUseCase(authorId).cachedIn(viewModelScope)

    init {
        setState {
            copy(
                authorName = savedStateHandle["authorName"] ?: "",
                authorAvatar = savedStateHandle["authorAvatar"] ?: "",
                authorNationality = savedStateHandle["authorNationality"] ?: "",
                authorBiography = savedStateHandle["authorBiography"] ?: ""
            )
        }
    }

    /**
     * Phát sự kiện mở màn hình chi tiết sách.
     */
    fun onBookClick(bookId: Int) {
        emitEvent(DetailAuthorEvent.NavigateToBookDetail(bookId))
    }

    /**
     * Đẩy thông báo lỗi lên lớp giao diện.
     */
    fun setErrorMessage(message: String) {
        emitError(message)
    }
}