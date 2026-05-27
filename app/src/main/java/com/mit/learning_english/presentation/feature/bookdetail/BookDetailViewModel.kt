package com.mit.learning_english.presentation.feature.bookdetail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.book.GetBookDetailByIdUseCase
import com.mit.learning_english.domain.usecase.book.UpdateFavoriteBookUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.presentation.feature.readbook.ReadBookArgs
import com.mit.learning_english.shared.FavoriteChangeNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel xử lý nghiệp vụ và quản lý trạng thái cho màn hình Chi tiết sách (BookDetailFragment).
 * Hỗ trợ tải dữ liệu chi tiết sách, yêu thích/bỏ yêu thích sách và điều hướng sang màn hình đọc sách.
 */
@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailByIdUseCase: GetBookDetailByIdUseCase,
    private val updateFavoriteBookUseCase: UpdateFavoriteBookUseCase,
    private val favoriteChangeNotifier: FavoriteChangeNotifier
) : BaseViewModel<BookDetailState, BookDetailEvent>(BookDetailState()) {
    
    /**
     * Tải thông tin chi tiết của sách từ hệ thống theo ID và cập nhật vào UI State.
     */
    fun getBookDetail(bookId: Int) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val startTime = System.currentTimeMillis()
            getBookDetailByIdUseCase(bookId).onSuccess { book ->
                setState { 
                    copy(
                        id = book.id,
                        title = book.title,
                        language = book.language,
                        coverUrl = book.coverUrl,
                        genresName = book.genresName,
                        authorsName = book.authorsName,
                        progressPercent = book.progressPercent,
                        chapters = book.chapters,
                        lastReadNumberPage = book.lastReadNumberPage,
                        isFavorite = book.isFavorite
                    ) 
                }
                Log.d("isFavorite", book.isFavorite.toString())
            }.onError { error ->
                emitError(error.message)
            }
            val elapsed = System.currentTimeMillis() - startTime
            if (elapsed < 1000) {
                delay(1000 - elapsed)
            }
            setLoading(false)
        }
    }

    /**
     * Phát sự kiện điều hướng sang màn hình Đọc sách (ReadBookFragment) kèm theo các thông tin cấu hình đọc.
     */
    fun navigateToReadBook(readMode: Int, chapterId: Int? = null) {
        val state = uiState.value
        if (state.id != 0) {
            emitEvent(
                BookDetailEvent.NavigateToReadBook(
                    ReadBookArgs(
                        bookId = state.id, chapterId = chapterId, readModeValue = readMode
                    )
                )
            )
        }
    }

    /**
     * Thêm hoặc xóa sách khỏi danh sách yêu thích của người dùng và thông báo cho các màn hình khác cập nhật lại dữ liệu.
     */
    fun clickedFavorite(){
        viewModelScope.launch(exceptionHandler) {
            val state = uiState.value
            if (state.id != 0) {
                updateFavoriteBookUseCase(state.id, !state.isFavorite).onSuccess { isFavorite ->
                    setState { copy(isFavorite = isFavorite) }
                    favoriteChangeNotifier.notifyChanged()
                }
                    .onError { error ->
                        emitError(error.message)
                    }
            }
        }
    }

}