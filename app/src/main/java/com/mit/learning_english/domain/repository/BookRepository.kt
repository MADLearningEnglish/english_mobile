package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooksByGenres(genresId: Int): Flow<PagingData<Book>>

    suspend fun getBooksRecommend(): Result<List<Book>>

    suspend fun getGenres(): Result<List<Genre>>

    suspend fun getBooksHistory(): Flow<PagingData<BookHistory>>

}
