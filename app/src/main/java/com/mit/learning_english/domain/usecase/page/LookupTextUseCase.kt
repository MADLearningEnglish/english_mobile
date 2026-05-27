package com.mit.learning_english.domain.usecase.page

import com.mit.learning_english.domain.model.TextLookupResult
import com.mit.learning_english.domain.repository.PageRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

/**
 * LookupTextUseCase – UseCase tầng Domain cho tính năng tra cứu từ vựng.
 *
 * Nhận chuỗi text (từ/cụm từ) và gọi [PageRepository.lookupText],
 * sau đó trả về kết quả hoặc lỗi dưới dạng [Result<TextLookupResult>].
 */
class LookupTextUseCase @Inject constructor(
    private val pageRepository: PageRepository
) {
    suspend operator fun invoke(text: String): Result<TextLookupResult> {
        return pageRepository.lookupText(text)
    }
}
