package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Page
import kotlinx.coroutines.flow.Flow

interface PageRepository {
    fun getPagesByBook(bookId: Int, totalPages: Int): Flow<PagingData<Page>>
}
