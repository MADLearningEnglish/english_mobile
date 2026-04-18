package com.mit.learning_english.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.mapper.toPage
import com.mit.learning_english.data.remote.api.PageApiService
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.shared.UiErrorKey

class PagePagingSource(
    private val pageApi: PageApiService,
    private val bookId: Int,
    private val totalPages: Int
) : PagingSource<Int, Page>() {

    override val jumpingSupported = true

    override fun getRefreshKey(state: PagingState<Int, Page>): Int? {

        return state.anchorPosition?.let { position ->
            (position / state.config.pageSize)*state.config.pageSize
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Page> {
       val start = if ((params.key ?: -1) >= 0) params.key!! else 0
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
                Log.d("PagePagingSource",pages.toString())
                LoadResult.Page(
                    data = pages,
                    prevKey = if (start > 0) maxOf(start - loadSize, 0) else null,
                    nextKey = if (end < totalPages) end else null,
                    itemsBefore = start,
                    itemsAfter = maxOf(totalPages - end, 0)
                )
            } else {
                LoadResult.Error(Exception(body?.message ?: UiErrorKey.FAILED_LOAD_PAGES))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
