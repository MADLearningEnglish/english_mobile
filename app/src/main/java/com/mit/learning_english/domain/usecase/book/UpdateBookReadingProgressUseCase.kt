package com.mit.learning_english.domain.usecase.book

import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class UpdateBookReadingProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(
        bookId: Int,
        lastReadPageNumber: Int,
        totalPages: Int,
        durationSeconds: Int?
    ): Result<Unit> {
        return bookRepository.updateReadingProgress(
            bookId, lastReadPageNumber, totalPages, durationSeconds
        )
    }
}
