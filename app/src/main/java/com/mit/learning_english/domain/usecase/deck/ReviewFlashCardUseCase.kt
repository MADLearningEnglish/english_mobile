package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.MasteryLevel
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class ReviewFlashCardUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deckId: Int, flashcardId: Int, level: MasteryLevel): Result<Unit> {
        return repository.reviewFlashcard(deckId, flashcardId, level)
    }
}