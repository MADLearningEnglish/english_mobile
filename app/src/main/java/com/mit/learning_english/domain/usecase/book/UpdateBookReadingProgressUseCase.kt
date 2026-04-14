package com.mit.learning_english.domain.usecase.book

import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.time.Duration

class UpdateBookReadingProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(
        bookId: Int,
        lastReadPageNumber: Int,
        lastRead: LocalDateTime,
        duration: Int
    ): Result<Unit> {
        return bookRepository.updateReadingProgress(
            bookId, lastReadPageNumber, lastRead, duration
        )
    }
}
