package com.mit.learning_english.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.mapper.toDomain
import com.mit.learning_english.data.remote.api.ProfileApiService
import com.mit.learning_english.domain.model.profile.LearningActivityItem

class LearningActivityHistoryPagingSource(
    private val api: ProfileApiService,
    private val filter: String,
    private val query: String?
) : PagingSource<Int, LearningActivityItem>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<Int, LearningActivityItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LearningActivityItem> {
        val page = params.key ?: 0
        return try {
            val response = api.getActivityHistory(
                filter = filter,
                q = query?.takeIf { it.isNotBlank() },
                page = page,
                size = PAGE_SIZE
            )
            if (!response.isSuccessful) {
                return LoadResult.Error(Exception(response.message()))
            }
            val body = response.body()
            val pageData = body?.data
            if (pageData == null) {
                return LoadResult.Error(Exception("Empty page"))
            }
            val items = pageData.content.map { it.toDomain() }
            val nextKey = if (page + 1 < pageData.totalPages) page + 1 else null
            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
