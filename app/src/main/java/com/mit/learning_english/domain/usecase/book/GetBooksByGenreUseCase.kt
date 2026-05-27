package com.mit.learning_english.domain.usecase.book

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Lấy danh sách sách theo thể loại dưới dạng phân trang.
 */
class GetBooksByGenreUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    /**
     * Trả về luồng dữ liệu sách theo `genreId`.
     */
    operator fun invoke(genreId: Int): Flow<PagingData<Book>> {
        return bookRepository.getBooksByGenres(genreId)
    }
}