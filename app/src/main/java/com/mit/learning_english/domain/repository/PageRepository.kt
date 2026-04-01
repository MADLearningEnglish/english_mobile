package com.mit.learning_english.domain.repository

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.model.TextLookupResult
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface PageRepository {
    suspend fun getPagesByBook(bookId: Int, totalPages: Int, initialKey: Int = 0): Flow<PagingData<Page>>
    suspend fun lookupText(text: String): Result<TextLookupResult>
}
