package com.mit.learning_english.presentation.feature.recommendbook

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.usecase.book.GetBookRecommendPagingUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * ViewModel xử lý nghiệp vụ cho màn hình hiển thị danh sách sách đề xuất (phân trang).
 */
@HiltViewModel
class RecommendBookViewModel @Inject constructor(
    getBookRecommendPagingUseCase: GetBookRecommendPagingUseCase
) : BaseViewModel<RecommendBookState, RecommendBookEvent>(RecommendBookState()) {

    /**
     * Luồng PagingData tải danh sách sách đề xuất hỗ trợ phân trang từ hệ thống.
     */
    val recommendBooks: Flow<PagingData<Book>> =
        getBookRecommendPagingUseCase().cachedIn(viewModelScope)

    /**
     * Phát sự kiện điều hướng sang màn hình Chi tiết sách khi cuốn sách được chọn.
     */
    fun onBookClick(bookId: Int) {
        emitEvent(RecommendBookEvent.NavigateToBookDetail(bookId))
    }
}
