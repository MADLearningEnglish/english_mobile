package com.mit.learning_english.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.mapper.toPage
import com.mit.learning_english.data.remote.api.PageApiService
import com.mit.learning_english.domain.model.Page

class PagePagingSource(
    private val pageApi: PageApiService,
    private val bookId: Int,
    private val totalPages: Int
) : PagingSource<Int, Page>() {

    override val jumpingSupported = true

    override fun getRefreshKey(state: PagingState<Int, Page>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Page> {
        val start = params.key ?: 0
        val loadSize = params.loadSize
        val end = minOf(start + loadSize, totalPages)

        if (start >= totalPages) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null,
                itemsBefore = start,
                itemsAfter = 0
            )
        }

        return try {
            val response = pageApi.getPagesByBook(bookId, offset = start, limit = end - start)
            val body = response.body()
            if (response.isSuccessful && body?.data != null) {
                val pages = body.data.map { it.toPage() }.sortedBy { it.number }
                LoadResult.Page(
                    data = pages,
                    prevKey = if (start > 0) start - loadSize else null,
                    nextKey = if (end < totalPages) end else null,
                    itemsBefore = start,
                    itemsAfter = maxOf(totalPages - end, 0)
                )
            } else {
                LoadResult.Error(Exception(body?.message ?: "Failed to load pages"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
