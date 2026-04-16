package com.mit.learning_english.presentation.feature.bookdetail

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.book.GetBookDetailByIdUseCase
import com.mit.learning_english.domain.usecase.book.UpdateFavoriteBookUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.presentation.feature.readbook.ReadBookArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val getBookDetailByIdUseCase: GetBookDetailByIdUseCase,
    private val updateFavoriteBookUseCase: UpdateFavoriteBookUseCase
) : BaseViewModel<BookDetailState, BookDetailEvent>(BookDetailState()) {
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

    fun clickedFavorite(){
        viewModelScope.launch(exceptionHandler) {
            val state = uiState.value
            if (state.id != 0) {
                updateFavoriteBookUseCase(state.id, !state.isFavorite).onSuccess { isFavorite ->
                    setState { copy(isFavorite = isFavorite) }
                }
                    .onError { error ->
                        emitError(error.message)
                    }
            }
        }
    }

}