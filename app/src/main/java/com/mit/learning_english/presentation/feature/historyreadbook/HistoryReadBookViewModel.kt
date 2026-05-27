package com.mit.learning_english.presentation.feature.historyreadbook

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.usecase.book.GetRecentlyReadBookUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
/**
 * ViewModel cung cấp dữ liệu lịch sử đọc và xử lý điều hướng.
 */
class HistoryReadBookViewModel @Inject constructor(
    getRecentlyReadBookUseCase: GetRecentlyReadBookUseCase
) : BaseViewModel<HistoryReadBookState, HistoryReadBookEvent>(HistoryReadBookState()) {

    /**
     * Luồng danh sách sách đã đọc gần đây theo dạng phân trang.
     */
    val historyBooks: Flow<PagingData<BookReponse>> =
        getRecentlyReadBookUseCase().cachedIn(viewModelScope)

    /**
     * Phát sự kiện mở chi tiết sách khi người dùng chọn item.
     */
    fun onBookClick(bookId: Int) {
        emitEvent(HistoryReadBookEvent.NavigateToBookDetail(bookId))
    }

    /**
     * Cập nhật trạng thái loading cho màn hình.
     */
    fun loading(isLoading:Boolean){
        setLoading(isLoading)
    }
}