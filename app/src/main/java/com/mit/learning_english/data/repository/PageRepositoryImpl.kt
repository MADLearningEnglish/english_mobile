package com.mit.learning_english.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.paging.PagePagingSource
import com.mit.learning_english.data.remote.api.PageApiService
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.PageRepository
import com.mit.learning_english.shared.Constant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PageRepositoryImpl @Inject constructor(
    private val pageApi: PageApiService
) : PageRepository {


    override suspend fun getPagesByBook(bookId: Int, totalPages: Int, initialKey: Int): Flow<PagingData<Page>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constant.PAGE_SIZE_PAGE,
                prefetchDistance = 2,
                enablePlaceholders = true,
                initialLoadSize = Constant.PAGE_SIZE_PAGE,
                maxSize = Constant.MAX_SIZE_PAGE,
                jumpThreshold = Constant.JUMP_THRESHOLD
            ),
            initialKey = initialKey,
            pagingSourceFactory = { PagePagingSource(pageApi, bookId, totalPages) }
        ).flow
    }
}
