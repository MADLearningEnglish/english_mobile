package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class LogDeckStudyCompleteUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(
        deckId: Int,
        durationSeconds: Int,
        cardsReviewed: Int?,
        quizCorrect: Int?,
        quizTotal: Int?
    ): Result<Unit> {
        return repository.postStudyComplete(
            deckId, durationSeconds, cardsReviewed, quizCorrect, quizTotal
        )
    }
}
