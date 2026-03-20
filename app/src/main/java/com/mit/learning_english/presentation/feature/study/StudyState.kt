package com.mit.learning_english.presentation.feature.study

import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.presentation.base.BaseUiState

data class StudyState(
    val deckTitle: String = "",
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isComplete: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null
) : BaseUiState<StudyState> {

    override fun copyWith(isLoading: Boolean?, errorMessage: String?) = copy(
        isLoading = isLoading ?: this.isLoading,
        errorMessage = errorMessage ?: this.errorMessage
    )

    val currentFlashcard: Flashcard? get() = flashcards.getOrNull(currentIndex)
    val totalCount: Int get() = flashcards.size
    val progressText: String get() = if (totalCount == 0) "0 / 0" else "${currentIndex + 1} / $totalCount"
}
