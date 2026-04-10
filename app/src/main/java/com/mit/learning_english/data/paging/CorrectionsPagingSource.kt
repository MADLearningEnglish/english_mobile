package com.mit.learning_english.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mit.learning_english.data.mapper.toUserCorrectionItem
import com.mit.learning_english.data.remote.api.ProfileApiService
import com.mit.learning_english.domain.model.profile.UserCorrectionItem

class CorrectionsPagingSource(
    private val api: ProfileApiService,
    private val filter: String,
    private val query: String?
) : PagingSource<Int, UserCorrectionItem>() {

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun getRefreshKey(state: PagingState<Int, UserCorrectionItem>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserCorrectionItem> {
        val page = params.key ?: 0
        return try {
            val response = api.getCorrections(
                filter = filter,
                q = query?.takeIf { it.isNotBlank() },
                page = page,
                size = PAGE_SIZE
            )
            if (!response.isSuccessful) {
                return LoadResult.Error(Exception(response.message()))
            }
            val pageData = response.body()?.data
            if (pageData == null) {
                return LoadResult.Error(Exception("Empty page"))
            }
            val items = pageData.content.mapNotNull { it.toUserCorrectionItem() }
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
