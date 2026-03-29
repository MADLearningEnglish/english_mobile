package com.mit.learning_english.presentation.feature.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.usecase.book.GetBookRecommendUseCase
import com.mit.learning_english.domain.usecase.book.GetRecentlyReadBookUseCase
import com.mit.learning_english.domain.usecase.genre.GetGenresUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBookRecommendUseCase: GetBookRecommendUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val getRecentlyReadBookUseCase: GetRecentlyReadBookUseCase
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {
    val recentBooks: Flow<PagingData<BookReponse>> =
        getRecentlyReadBookUseCase().cachedIn(viewModelScope)

    init {
        fetchRecommendBooks()
        fetchGenres()
    }

    fun fetchRecommendBooks() {
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isRecommendBooksLoading = true) }
            val result = getBookRecommendUseCase.invoke()
            result.onSuccess { data ->
                setState { copy(recommendBooks = data) }
            }.onLoading {
                setState { copy(isRecommendBooksLoading = true) }
            }.onError { error ->
                emitError(error.message ?: "Unknown error")
            }
            setState { copy(isRecommendBooksLoading = false) }
        }
    }

    fun fetchGenres() {
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isGenresLoading = true) }
            val result = getGenresUseCase.invoke()
            result.onSuccess { data ->
                setState { copy(genres = data) }
            }.onLoading {
                setState { copy(isGenresLoading = true) }
            }.onError { error ->
                emitError(error.message ?: "Unknown error")
            }
            setState { copy(isGenresLoading = false) }
        }
    }

    fun navigateToSearchBook() {
        emitEvent(HomeEvent.NavigateToSearchFragment)
    }

    fun navigateToBookDetail(bookId: Int) {
        emitEvent(HomeEvent.NavigateToBookDetailFragment(bookId))
    }

    fun setErrorMessage(message: String) {
        emitError(message)
    }

}
