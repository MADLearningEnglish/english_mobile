package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

/**
 * UseCase lấy danh sách toàn bộ các flashcard của một bộ thẻ cụ thể.
 * Được sử dụng khi bắt đầu một phiên học lật thẻ mới (chế độ Quizlet ngẫu nhiên).
 */
class GetStudyFlashCardsUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    /**
     * Gọi API lấy toàn bộ thẻ bằng deckId.
     * @param deckId ID của bộ thẻ.
     * @return Danh sách Flashcard gói trong đối tượng Result.
     */
    suspend operator fun invoke(deckId: Int): Result<List<Flashcard>> {
        return repository.getAllFlashcards(deckId)
    }
}