package com.mit.learning_english.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toBook
import com.mit.learning_english.data.mapper.toBookDetail
import com.mit.learning_english.data.mapper.toPage
import com.mit.learning_english.data.paging.BookByGenresPagingSource
import com.mit.learning_english.data.paging.BookHistoryPagingSource
import com.mit.learning_english.data.paging.SearchBookPagingSource
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApiService, private val resultMapper: ResultMapper
) : BookRepository {
    override suspend fun getBooksByGenres(genresId: Int): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, prefetchDistance = 5, enablePlaceholders = false
            ), pagingSourceFactory = { BookByGenresPagingSource(bookApi, genresId) }).flow
    }

    override suspend fun getBooksRecommend(): Result<List<Book>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getBooksRecommend()
                Log.d("BookRepositoryImpl", response.body().toString())
                val result = resultMapper.fromBaseResponse(response).map { list ->
                    list.map {
                        it.toBook()
                    }
                }
                Log.d("BookRepositoryImpl", result.toString())
                result
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override fun getBooksHistory(): Flow<PagingData<BookReponse>> {
        return Pager(
            config = PagingConfig(
                pageSize = BookHistoryPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = BookHistoryPagingSource.PAGE_SIZE
            ), pagingSourceFactory = { BookHistoryPagingSource(bookApi) }).flow
    }

    override suspend fun getBookDetailById(bookId: Int): Result<BookDetail> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getBookDetailById(bookId)
                resultMapper.fromBaseResponse(response).map { it.toBookDetail() }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun updateFavoriteBook(bookId: Int, isFavorite: Boolean): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.updateFavoriteBook(bookId = bookId, isFavorite = isFavorite)
                resultMapper.fromBaseResponse(response)
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun getPagesByChapter(
        chapterId: Int, pageNumbers: List<Int>
    ): Result<List<Page>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getPagesReadBook(chapterId, pageNumbers)
                resultMapper.fromBaseResponse(response).map { list -> list.map { it.toPage() } }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override fun searchBooks(keyword: String): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = SearchBookPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = SearchBookPagingSource.PAGE_SIZE
            ),
            pagingSourceFactory = { SearchBookPagingSource(bookApi, keyword) }
        ).flow
    }
}
