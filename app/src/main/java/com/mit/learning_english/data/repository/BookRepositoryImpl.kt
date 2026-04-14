package com.mit.learning_english.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toBook
import com.mit.learning_english.data.mapper.toBookDetail
import com.mit.learning_english.data.mapper.toBookHistory
import com.mit.learning_english.data.paging.BookByGenresPagingSource
import com.mit.learning_english.data.paging.BookHistoryPagingSource
import com.mit.learning_english.data.paging.AuthorPagingSource
import com.mit.learning_english.data.paging.AuthorBooksPagingSource
import com.mit.learning_english.data.paging.FavoriteBookPagingSource
import com.mit.learning_english.data.paging.RecommendBookPagingSource
import com.mit.learning_english.data.paging.SearchBookPagingSource
import com.mit.learning_english.data.remote.dto.BookReadingProgressRequestDto
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookReponse
import com.mit.learning_english.domain.model.Author
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookApi: BookApiService, private val resultMapper: ResultMapper
) : BookRepository {
    override fun getAuthorsPaging(): Flow<PagingData<Author>> {
        return Pager(
            config = PagingConfig(
                pageSize = AuthorPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = AuthorPagingSource.PAGE_SIZE
            ),
            pagingSourceFactory = { AuthorPagingSource(bookApi) }
        ).flow
    }

    override fun getBooksByGenres(genresId: Int): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = BookByGenresPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = BookByGenresPagingSource.PAGE_SIZE
            ), pagingSourceFactory = { BookByGenresPagingSource(bookApi, genresId) }).flow
    }

    override suspend fun getBooksRecommend(): Result<List<Book>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getBooksRecommend(page = 1, limit = 10)
                Log.d("BookRepositoryImpl", response.body().toString())
                val result = resultMapper.fromBaseResponse(response).map { pageResponse ->
                    pageResponse.data.map {
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

    override fun getBooksRecommendPaging(): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = RecommendBookPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = RecommendBookPagingSource.PAGE_SIZE
            ),
            pagingSourceFactory = { RecommendBookPagingSource(bookApi) }
        ).flow
    }

    override fun getFavoriteBooksPaging(): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = FavoriteBookPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = FavoriteBookPagingSource.PAGE_SIZE
            ),
            pagingSourceFactory = { FavoriteBookPagingSource(bookApi) }
        ).flow
    }

    override fun getBooksByAuthorPaging(authorId: Int): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = AuthorBooksPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = AuthorBooksPagingSource.PAGE_SIZE
            ),
            pagingSourceFactory = { AuthorBooksPagingSource(bookApi, authorId) }
        ).flow
    }

    override suspend fun getRecommendByTopic(): Result<List<Book>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getRecommendByTopic()
                resultMapper.fromBaseResponse(response).map { list ->
                    list.map { it.toBook() }
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun getRecommendByAuthor(): Result<List<Book>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getRecommendByAuthor()
                resultMapper.fromBaseResponse(response).map { list ->
                    list.map { it.toBook() }
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun getReadingInProgress(): Result<List<BookReponse>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = bookApi.getReadingInProgress()
                resultMapper.fromBaseResponse(response).map { list ->
                    list.map { it.toBookHistory() }
                }
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
                Log.d("BookRepositoryImpl", response.body().toString())
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

    override fun searchBooks(keyword: String): Flow<PagingData<Book>> {
        return Pager(
            config = PagingConfig(
                pageSize = SearchBookPagingSource.PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = SearchBookPagingSource.PAGE_SIZE
            ), pagingSourceFactory = { SearchBookPagingSource(bookApi, keyword) }).flow
    }

    override suspend fun updateReadingProgress(
        bookId: Int,
        lastReadPageNumber: Int,
        lastRead: LocalDateTime,
        duration:Int
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val body = BookReadingProgressRequestDto(
                    lastReadPageNumber = lastReadPageNumber,
                    lastRead = lastRead,
                    duration =duration
                )
                val response = bookApi.updateReadingProgress(bookId, body)
                if (response.isSuccessful) {
                    Result.Success(Unit)
                } else {
                    Result.Error(response.message() ?: "update progress failed")
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }
}
