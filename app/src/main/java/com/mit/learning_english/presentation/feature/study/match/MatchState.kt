package com.mit.learning_english.presentation.feature.study.match

data class MatchState(
    val flashcards: List<com.mit.learning_english.domain.model.Flashcard> = emptyList(),
    val currentRound: Int = 1,
    val totalRounds: Int = 6,
    val cardsOnScreen: List<MatchCard> = emptyList(),
    val selectedFirstCardId: String? = null,
    val isComplete: Boolean = false,
    val isLoading: Boolean = false,
    val isPreGame: Boolean = true
)

data class MatchCard(
    val id: String,
    val flashcardId: String,
    val text: String,
    val isTerm: Boolean,
    val state: CardState = CardState.UNSELECTED,
    val imageUrl: String? = null
)

enum class CardState {
    UNSELECTED, SELECTED, CORRECT, INCORRECT, HIDDEN
}
