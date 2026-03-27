package com.mit.learning_english.domain.usecase

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(keyword: String): Flow<PagingData<Book>> {
        return bookRepository.searchBooks(keyword)
    }
}
