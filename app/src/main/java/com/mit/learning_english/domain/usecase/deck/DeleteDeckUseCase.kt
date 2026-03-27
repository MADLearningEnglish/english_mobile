package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class DeleteDeckUseCase @Inject constructor(
    private val deckRepository: DeckRepository
) {
    suspend operator fun invoke(deckId: Int): Result<Unit> {
        return deckRepository.deleteDeck(deckId)
    }
}
