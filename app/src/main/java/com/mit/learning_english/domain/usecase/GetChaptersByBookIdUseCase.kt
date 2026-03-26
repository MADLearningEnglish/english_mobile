package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.Chapter
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetChaptersByBookIdUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Int): Result<List<Chapter>> {
        val result = bookRepository.getBookDetailById(bookId)
        return when (result) {
            is Result.Success -> Result.Success(result.data.chapters)
            is Result.Error -> Result.Error(result.message, result.code, result.exception)
            is Result.Loading -> Result.Loading
        }
    }
}