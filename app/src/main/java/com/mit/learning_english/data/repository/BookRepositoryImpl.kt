package com.mit.learning_english.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.paging.BookByGenresPagingSource
import com.mit.learning_english.data.paging.BookHistoryPagingSource
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.Book
import com.mit.learning_english.domain.model.BookDetail
import com.mit.learning_english.domain.model.BookHistory
import com.mit.learning_english.domain.model.Genre
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
        val response = bookApi.getBooksRecommend()
        return resultMapper.fromBaseResponse(response)
    }

    override suspend fun getGenres(): Result<List<Genre>> {
        val response = bookApi.getGenres()
        return resultMapper.fromBaseResponse(response)
    }

    override suspend fun getBooksHistory(): Flow<PagingData<BookHistory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, prefetchDistance = 5, enablePlaceholders = false
            ), pagingSourceFactory = { BookHistoryPagingSource(bookApi) }).flow
    }

    override suspend fun getBookDetailById(bookId: Int): Result<BookDetail> {
        val response = bookApi.getBookDetailById(bookId)
        return resultMapper.fromBaseResponse(response)
    }

    override suspend fun updateFavoriteBook(isFavorite: Boolean): Result<Boolean> {

    }

    override suspend fun getPagesByChapter(
        chapterId: Int,
        pageNumbers: List<Int>
    ): Result<List<Page>> {
        val response = bookApi.getPagesByChapter(chapterId, pageNumbers)
        return resultMapper.fromBaseResponse(response)
    }
}
