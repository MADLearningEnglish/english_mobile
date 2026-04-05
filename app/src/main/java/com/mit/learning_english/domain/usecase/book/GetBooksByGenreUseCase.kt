package com.mit.learning_english.domain.usecase.book

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBooksByGenreUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(genreId:Int): Flow<PagingData<Book>> {
        return bookRepository.getBooksByGenres(genreId)
    }
}