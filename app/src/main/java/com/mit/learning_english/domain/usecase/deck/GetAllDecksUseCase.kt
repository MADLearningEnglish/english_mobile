package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetAllDecksUseCase @Inject constructor(
    private val deckRepository: DeckRepository
) {
    suspend operator fun invoke(search: String? = null): Result<List<Deck>> {
        return deckRepository.getAllDecks(search)
    }
}
