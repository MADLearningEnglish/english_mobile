package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetDeckByIdUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deckId: Int): Result<Deck> {
        return repository.getDeckById(deckId)
    }
}
