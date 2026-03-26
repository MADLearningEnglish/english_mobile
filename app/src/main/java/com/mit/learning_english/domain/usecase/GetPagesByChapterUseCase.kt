package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetPagesByChapterUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(
        bookId: Int,
        pageNumbers: List<Int>,
    ): Result<List<Page>> {
        return bookRepository.getPagesByChapter(bookId, pageNumbers)
    }
}
