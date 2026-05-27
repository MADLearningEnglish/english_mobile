package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

/**
 * UseCase thực thi logic lấy danh sách các bộ thẻ từ Repository.
 * Được sử dụng ở màn hình danh sách bộ thẻ.
 */
class GetAllDecksUseCase @Inject constructor(
    private val deckRepository: DeckRepository
) {
    /**
     * Gọi Repository để lấy dữ liệu.
     * @param search Từ khóa tìm kiếm tùy chọn.
     * @return Danh sách bộ thẻ gói trong đối tượng Result.
     */
    suspend operator fun invoke(search: String? = null): Result<List<Deck>> {
        return deckRepository.getAllDecks(search)
    }
}
