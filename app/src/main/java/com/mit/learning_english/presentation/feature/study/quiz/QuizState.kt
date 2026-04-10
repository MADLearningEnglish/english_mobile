package com.mit.learning_english.presentation.feature.study.quiz

import com.mit.learning_english.domain.model.QuizQuestion

data class QuizState(
    val deckTitle: String = "",
    val flashcards: List<com.mit.learning_english.domain.model.Flashcard> = emptyList(),
    val quizQueue: List<QuizQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedAnswer: String? = null,
    val isAnswerRevealed: Boolean = false,
    val isComplete: Boolean = false,
    val correctCount: Int = 0,
    val totalCount: Int = 0
) {
    val currentQuestion: QuizQuestion? get() = quizQueue.getOrNull(currentIndex)
}
