package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.CreateDeckRequest
import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.MasteryLevel
import com.mit.learning_english.domain.model.StudyResult
import com.mit.learning_english.domain.util.Result

interface DeckRepository {
    suspend fun createDeck(request: CreateDeckRequest): Result<Deck>
    suspend fun getAllDecks(search: String? = null): Result<List<Deck>>
    suspend fun getFlashcardsToStudy(deckId: Int): Result<List<Flashcard>>
    suspend fun getAllFlashcards(deckId: Int): Result<List<Flashcard>>

    suspend fun reviewFlashcard(deckId: Int, flashcardId: Int, level: MasteryLevel): Result<Unit>
    suspend fun getStudyResults(deckId: Int): Result<StudyResult>
    suspend fun getDeckById(deckId: Int): Result<Deck>
    suspend fun updateDeck(deckId: Int, request: com.mit.learning_english.domain.model.UpdateDeckRequest): Result<Deck>
    suspend fun deleteDeck(deckId: Int): Result<Unit>

    /** Báo cáo hoàn thành phiên học flashcard (heatmap / profile). */
    suspend fun postStudyComplete(
        deckId: Int,
        durationSeconds: Int,
        cardsReviewed: Int?,
        quizCorrect: Int?,
        quizTotal: Int?
    ): Result<Unit>
}