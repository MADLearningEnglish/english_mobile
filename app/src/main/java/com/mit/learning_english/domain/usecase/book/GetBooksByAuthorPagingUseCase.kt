package com.mit.learning_english.domain.usecase.book

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBooksByAuthorPagingUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(authorId: Int): Flow<PagingData<Book>> {
        return bookRepository.getBooksByAuthorPaging(authorId)
    }
}
