package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(): Result<List<Genre>> {
        return bookRepository.getGenres()
    }
}

