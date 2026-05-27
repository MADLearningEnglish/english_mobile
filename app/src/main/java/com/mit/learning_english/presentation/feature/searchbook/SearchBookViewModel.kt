package com.mit.learning_english.presentation.feature.searchbook

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.usecase.book.SearchBookUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * ViewModel xử lý nghiệp vụ tìm kiếm sách (hỗ trợ phân trang và debounce).
 */
@HiltViewModel
class SearchBookViewModel @Inject constructor(
    private val searchBookUseCase: SearchBookUseCase
) : BaseViewModel<SearchBookState, SearchBookEvent>(SearchBookState()) {
    private val _searchQuery = MutableStateFlow("")

    /**
     * Luồng PagingData kết quả tìm kiếm sách. 
     * Áp dụng cơ chế debounce (300ms) để trì hoãn tìm kiếm trong khi người dùng đang gõ,
     * lọc các truy vấn trống và thực hiện tìm kiếm qua UseCase.
     */
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchResults: Flow<PagingData<Book>> =
        _searchQuery.debounce(300).filter { it.isNotBlank() }.flatMapLatest { query ->
                searchBookUseCase(query)
            }.cachedIn(viewModelScope)

    /**
     * Cập nhật từ khóa tìm kiếm mới khi người dùng thay đổi văn bản trên thanh tìm kiếm.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        setState { copy(searchQuery = query) }
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Chi tiết sách khi người dùng click vào một cuốn sách.
     */
    fun onBookClicked(bookId: Int) {
        emitEvent(SearchBookEvent.NavigateToBookDetail(bookId))
    }
}
