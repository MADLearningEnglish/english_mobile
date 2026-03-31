package com.mit.learning_english.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mit.learning_english.data.paging.PagePagingSource
import com.mit.learning_english.data.remote.api.PageApiService
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.PageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PageRepositoryImpl @Inject constructor(
    private val pageApi: PageApiService
) : PageRepository {

    companion object {
        const val PAGE_SIZE = 5
    }

    override fun getPagesByBook(bookId: Int, totalPages: Int): Flow<PagingData<Page>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 3,
                enablePlaceholders = true,
                initialLoadSize = PAGE_SIZE,
                maxSize = 50,
            ),
            pagingSourceFactory = { PagePagingSource(pageApi, bookId, totalPages) }
        ).flow
    }
}
