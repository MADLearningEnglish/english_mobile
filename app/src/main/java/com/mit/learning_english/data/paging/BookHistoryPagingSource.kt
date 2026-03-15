package com.mit.learning_english.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.BookHistory

class BookHistoryPagingSource(
    private val api: BookApiService
) : PagingSource<Int, BookHistory>() {
    override fun getRefreshKey(state: PagingState<Int, BookHistory>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1) ?: state.closestPageToPosition(
                anchor
            )?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookHistory> {
        val page = params.key ?: 1
        return try {
            val response = api.getBookHistory(page = page, size = params.loadSize)
            if (response.isSuccessful) {
                val items: List<BookHistory> = response.body()?.data ?: emptyList()
                LoadResult.Page(
                    data = items,
                    prevKey = if (page == 1) null else page - 1,
                    nextKey = if (items.isEmpty()) null else page + 1
                )
            } else {
                LoadResult.Error(Exception("API error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
