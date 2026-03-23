package com.mit.learning_english.presentation.feature.bookdetail

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.GetBookDetailByIdUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.presentation.feature.readbook.ReadBookArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailByIdUseCase: GetBookDetailByIdUseCase
) : BaseViewModel<BookDetailState, BookDetailEvent>(BookDetailState()) {
    fun getBookDetail(bookId: Int) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            getBookDetailByIdUseCase(bookId).onSuccess { book ->
                setState { copyWith(isLoading = false, errorMessage = null, book = book) }
            }.onError { error ->
                setError(error.message)
                setLoading(false)
            }
        }
    }

    fun navigateToReadBook(readMode: Int, chapterId: Int? = null) {
        val state = uiState.value
        state.book?.let {
            emitEvent(
                BookDetailEvent.NavigateToReadBook(
                    ReadBookArgs(
                        bookId = state.book.id,
                        chapterId = chapterId,
                        readModeValue = readMode
                    )
                )
            )
        }
    }
}