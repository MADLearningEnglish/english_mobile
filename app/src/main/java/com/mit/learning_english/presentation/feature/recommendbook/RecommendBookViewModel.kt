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

@HiltViewModel
class RecommendBookViewModel @Inject constructor(
    getBookRecommendPagingUseCase: GetBookRecommendPagingUseCase
) : BaseViewModel<RecommendBookState, RecommendBookEvent>(RecommendBookState()) {

    val recommendBooks: Flow<PagingData<Book>> =
        getBookRecommendPagingUseCase().cachedIn(viewModelScope)

    fun onBookClick(bookId: Int) {
        emitEvent(RecommendBookEvent.NavigateToBookDetail(bookId))
    }
}
