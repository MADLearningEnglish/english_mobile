package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetBookDetailByIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Int): Result<BookDetail> {
        return bookRepository.getBookDetailById(bookId)
    }
}