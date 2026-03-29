package com.mit.learning_english.domain.usecase.page

import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.domain.repository.PageRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetPagesByChapterUseCase @Inject constructor(
    private val pageRepository: PageRepository
) {
    suspend operator fun invoke(
        chapterId: Int,
        pageNumbers: List<Int>,
    ): Result<List<Page>> {
        return pageRepository.getPagesByChapter(chapterId = chapterId, pageNumbers)
    }
}