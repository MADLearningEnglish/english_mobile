package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun getBooksByGenres(genresId: Int): Flow<PagingData<Book>>

    suspend fun getBooksRecommend(): Result<List<Book>>

    fun getBooksHistory(): Flow<PagingData<BookReponse>>

    suspend fun getBookDetailById(bookId: Int): Result<BookDetail>

    suspend fun updateFavoriteBook(bookId: Int, isFavorite: Boolean): Result<Boolean>

    suspend fun getPagesByChapter(chapterId: Int, pageNumbers: List<Int>): Result<List<Page>>

    fun searchBooks(keyword: String): Flow<PagingData<Book>>

}
