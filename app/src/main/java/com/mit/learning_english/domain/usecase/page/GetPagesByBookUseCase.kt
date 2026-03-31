package com.mit.learning_english.domain.usecase.page

import androidx.paging.PagingData
import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.PageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagesByBookUseCase @Inject constructor(
    private val pageRepository: PageRepository
) {
    operator fun invoke(bookId: Int, totalPages: Int): Flow<PagingData<Page>> {
        return pageRepository.getPagesByBook(bookId, totalPages)
    }
}