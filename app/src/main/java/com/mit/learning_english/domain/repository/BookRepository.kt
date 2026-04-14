package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Author
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import kotlin.time.Duration

interface BookRepository {
    fun getAuthorsPaging(): Flow<PagingData<Author>>

    fun getBooksByGenres(genresId: Int): Flow<PagingData<Book>>

    suspend fun getBooksRecommend(): Result<List<Book>>

    fun getBooksRecommendPaging(): Flow<PagingData<Book>>

    fun getFavoriteBooksPaging(): Flow<PagingData<Book>>

    fun getBooksByAuthorPaging(authorId: Int): Flow<PagingData<Book>>

    suspend fun getRecommendByTopic(): Result<List<Book>>

    suspend fun getRecommendByAuthor(): Result<List<Book>>

    suspend fun getReadingInProgress(): Result<List<BookReponse>>

    fun getBooksHistory(): Flow<PagingData<BookReponse>>

    suspend fun getBookDetailById(bookId: Int): Result<BookDetail>

    suspend fun updateFavoriteBook(bookId: Int, isFavorite: Boolean): Result<Boolean>

    fun searchBooks(keyword: String): Flow<PagingData<Book>>

    suspend fun updateReadingProgress(
        bookId: Int,
        lastReadPageNumber: Int,
        lastRead: LocalDateTime,
        duration: Int
    ): Result<Unit>

}
