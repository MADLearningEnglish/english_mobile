package com.mit.learning_english.presentation.feature.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.domain.usecase.GetBookRecommendUseCase
import com.mit.learning_english.domain.usecase.GetGenresUseCase
import com.mit.learning_english.domain.usecase.GetRecentlyReadBookUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getBookRecommendUseCase: GetBookRecommendUseCase,
    private val getGenresUseCase: GetGenresUseCase,
    private val getRecentlyReadBookUseCase: GetRecentlyReadBookUseCase
) : BaseViewModel<HomeState, HomeEvent>(HomeState()) {
    
    private val _recentBooks = MutableStateFlow<PagingData<BookHistory>>(PagingData.empty())
    val recentBooks: StateFlow<PagingData<BookHistory>> = _recentBooks.asStateFlow()

    init {
        fetchRecommendBooks()
        fetchGenres()
        fetchRecentBooks()
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

    private fun fetchRecentBooks() {
        viewModelScope.launch {
            getRecentlyReadBookUseCase().cachedIn(viewModelScope).collectLatest {
                _recentBooks.value = it
            }
        }
    }

}
