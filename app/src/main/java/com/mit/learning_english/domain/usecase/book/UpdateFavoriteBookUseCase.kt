package com.mit.learning_english.domain.usecase.book

import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class UpdateFavoriteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Int, isFavorite: Boolean): Result<Boolean> {
        return bookRepository.updateFavoriteBook(bookId, isFavorite)
    }
}