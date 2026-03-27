package com.mit.learning_english.presentation.feature.study

import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.QuizQuestion

enum class StudyMode { FLASHCARD, QUIZ }

data class StudyState(
    val deckTitle: String = "",
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isComplete: Boolean = false,

    val studyMode: StudyMode = StudyMode.FLASHCARD,
    val currentQuestion: QuizQuestion? = null,
    val selectedAnswer: String? = null,
    val isAnswerRevealed: Boolean = false,
    val quizScore: Int = 0,
    val quizTotal: Int = 0,

    val quizQueue: List<QuizQuestion> = emptyList(),
    val quizCurrentInBatch: Int = 0,
    val quizBatchSize: Int = 0,
) {
    val currentFlashcard: Flashcard? get() = flashcards.getOrNull(currentIndex)
    val totalCount: Int get() = flashcards.size
    val progressText: String get() = if (totalCount == 0) "0 / 0" else "${currentIndex + 1} / $totalCount"
}
