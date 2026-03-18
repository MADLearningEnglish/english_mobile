package com.mit.learning_english.presentation.feature.vocabulary

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.data.remote.dto.BookRecommendResponse
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class VocabularyViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : BaseViewModel<VocabularyState, VocabularyEvent>(VocabularyState()) {
    // PagingData sống ngoài UIState — cached để tránh reset khi rotate màn hình
    val books: Flow<PagingData<BookRecommendResponse>> =
        bookRepository.getBooksByGenres().cachedIn(viewModelScope)
}

