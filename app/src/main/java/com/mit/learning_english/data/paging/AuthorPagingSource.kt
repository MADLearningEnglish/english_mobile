package com.mit.learning_english.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.mapper.toAuthor
import com.mit.learning_english.data.remote.api.BookApiService
import com.mit.learning_english.domain.model.Author

class AuthorPagingSource(
    private val api: BookApiService
) : PagingSource<Int, Author>() {

    companion object {
        const val PAGE_SIZE = 10
    }

    override fun getRefreshKey(state: PagingState<Int, Author>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Author> {
        val page = params.key ?: 1
        return try {
            val response = api.getAuthors(page = page, limit = PAGE_SIZE)
            if (response.isSuccessful) {
                val paginatedData = response.body()?.data
                val items = paginatedData?.data?.map { it.toAuthor() } ?: emptyList()
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
