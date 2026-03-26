package com.mit.learning_english.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.mapper.toBook
import com.mit.learning_english.data.mapper.toBookDetail
import com.mit.learning_english.data.mapper.toPage
import com.mit.learning_english.data.paging.BookByGenresPagingSource
import com.mit.learning_english.data.paging.BookHistoryPagingSource
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.BookRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow
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
        return try {
            val response = bookApi.getBooksRecommend()
            resultMapper.fromBaseResponse(response).map { list -> list.map { it.toBook() } }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun getBooksHistory(): Flow<PagingData<BookHistory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, prefetchDistance = 5, enablePlaceholders = false
            ), pagingSourceFactory = { BookHistoryPagingSource(bookApi) }).flow
    }

    override suspend fun getBookDetailById(bookId: Int): Result<BookDetail> {
        return try {
            val response = bookApi.getBookDetailById(bookId)
            resultMapper.fromBaseResponse(response).map { it.toBookDetail() }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun updateFavoriteBook(bookId: Int, isFavorite: Boolean): Result<Boolean> {
        return try {
            val response = bookApi.updateFavoriteBook(bookId = bookId, isFavorite = isFavorite)
            resultMapper.fromBaseResponse(response)
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun getPagesByChapter(
        chapterId: Int, pageNumbers: List<Int>
    ): Result<List<Page>> {
        return try {
            val response = bookApi.getPagesReadBook(chapterId, pageNumbers)
            resultMapper.fromBaseResponse(response).map { list -> list.map { it.toPage() } }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }
}
