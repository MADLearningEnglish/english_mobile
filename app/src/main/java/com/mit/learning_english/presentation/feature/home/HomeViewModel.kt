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
        viewModelScope.launch {
            setState { copyWith(isRecommendBooksLoading = true) }
            val result = getBookRecommendUseCase.invoke()
            result.onSuccess { data ->
                setState { copyWith(recommendBooks = data) }
            }.onLoading {
                setState { copyWith(isRecommendBooksLoading = true) }
            }.onError { error ->
                setError(error.message)
            }
            setState { copyWith(isRecommendBooksLoading = false) }
        }
    }

    fun fetchGenres() {
        viewModelScope.launch {
            setState { copyWith(isGenresLoading = true) }
            val result = getGenresUseCase.invoke()
            result.onSuccess { data ->
                setState { copyWith(genres = data) }
            }.onLoading {
                setState { copyWith(isGenresLoading = true) }
            }.onError { error ->
                setError(error.message)
            }
            setState { copyWith(isGenresLoading = false) }
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
