package com.mit.learning_english.domain.usecase.book

import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetReadingInProgressUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(): Result<List<BookReponse>> {
        return bookRepository.getReadingInProgress()
    }
}
