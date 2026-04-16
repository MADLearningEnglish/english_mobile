package com.mit.learning_english.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.mapper.toBook
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.Book

class FavoriteBookPagingSource(
    private val api: BookApiService
) : PagingSource<Int, Book>() {

    companion object {
        const val PAGE_SIZE = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        val page = params.key ?: 1
        return try {
            val response = api.getFavoriteBooks(page = page, limit = PAGE_SIZE)
            if (response.isSuccessful) {
                val paginatedData = response.body()?.data
                val items = paginatedData?.data?.map { it.toBook() } ?: emptyList()
                val total = paginatedData?.total ?: 0
                val hasNextPage = page * PAGE_SIZE < total
                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (hasNextPage) page + 1 else null
                )
            } else {
                LoadResult.Error(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
