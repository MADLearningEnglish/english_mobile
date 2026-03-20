package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.CreateDeckRequest
import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class CreateDeckUseCase @Inject constructor(
    private val deckRepository: DeckRepository
) {
    suspend operator fun invoke(request: CreateDeckRequest): Result<Deck> {
        return deckRepository.createDeck(request)
    }
}
