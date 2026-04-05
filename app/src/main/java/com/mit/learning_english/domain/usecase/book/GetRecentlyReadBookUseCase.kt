package com.mit.learning_english.domain.usecase.book

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentlyReadBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(): Flow<PagingData<BookReponse>> {
        return bookRepository.getBooksHistory()
    }
}