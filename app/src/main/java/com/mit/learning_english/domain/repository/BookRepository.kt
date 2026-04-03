package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooksByGenres(genresId: Int): Flow<PagingData<Book>>

    suspend fun getBooksRecommend(): Result<List<Book>>

    suspend fun getRecommendByTopic(): Result<List<Book>>

    suspend fun getRecommendByAuthor(): Result<List<Book>>

    suspend fun getReadingInProgress(): Result<List<BookReponse>>

    fun getBooksHistory(): Flow<PagingData<BookReponse>>

    suspend fun getBookDetailById(bookId: Int): Result<BookDetail>

    suspend fun updateFavoriteBook(bookId: Int, isFavorite: Boolean): Result<Boolean>

    fun searchBooks(keyword: String): Flow<PagingData<Book>>

}
