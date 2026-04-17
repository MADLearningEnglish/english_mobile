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
class HistoryReadBookViewModel @Inject constructor(
    getRecentlyReadBookUseCase: GetRecentlyReadBookUseCase
) : BaseViewModel<HistoryReadBookState, HistoryReadBookEvent>(HistoryReadBookState()) {

    val historyBooks: Flow<PagingData<BookReponse>> =
        getRecentlyReadBookUseCase().cachedIn(viewModelScope)

    fun onBookClick(bookId: Int) {
        emitEvent(HistoryReadBookEvent.NavigateToBookDetail(bookId))
    }
    fun loading(isLoading:Boolean){
        setLoading(isLoading)
    }
}