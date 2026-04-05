package com.mit.learning_english.domain.usecase.deck

import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.repository.DeckRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

class GetStudyFlashCardsUseCase @Inject constructor(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deckId: Int): Result<List<Flashcard>> {
        return repository.getAllFlashcards(deckId)
    }
}