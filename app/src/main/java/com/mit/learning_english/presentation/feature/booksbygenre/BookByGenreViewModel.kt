package com.mit.learning_english.presentation.feature.booksbygenre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.usecase.book.GetBooksByGenreUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class BookByGenreViewModel @Inject constructor(
    private val getBooksByGenreUseCase: GetBooksByGenreUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<BookByGenreState, BookByGenreEvent>(BookByGenreState) {

    private val genreId: Int = savedStateHandle.get<Int>("genreId") ?: 0

    val books: Flow<PagingData<Book>> = getBooksByGenreUseCase(genreId).cachedIn(viewModelScope)

    fun onBookClicked(bookId: Int) {
        emitEvent(BookByGenreEvent.NavigateToBookDetail(bookId))
    }
}
