package com.mit.learning_english.domain.usecase.book

import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetBookRecommendUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(): Result<List<Book>> {
        return bookRepository.getBooksRecommend()
    }
}