package com.mit.learning_english.presentation.feature.study

import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.QuizQuestion

data class StudyState(
    val deckTitle: String = "",
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isComplete: Boolean = false
) {
    val currentFlashcard: Flashcard? get() = flashcards.getOrNull(currentIndex)
    val totalCount: Int get() = flashcards.size
    val progressText: String get() = if (totalCount == 0) "0 / 0" else "${currentIndex + 1} / $totalCount"
}
