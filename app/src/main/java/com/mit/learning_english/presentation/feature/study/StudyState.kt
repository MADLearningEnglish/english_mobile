package com.mit.learning_english.presentation.feature.study

import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.QuizQuestion
import com.mit.learning_english.presentation.base.BaseUiState

/** Chế độ hiển thị hiện tại trong phiên học */
enum class StudyMode { FLASHCARD, QUIZ }

data class StudyState(
    val deckTitle: String = "",
    val flashcards: List<Flashcard> = emptyList(),
    val currentIndex: Int = 0,
    val isFlipped: Boolean = false,
    val isComplete: Boolean = false,

    // Quiz
    val studyMode: StudyMode = StudyMode.FLASHCARD,
    val currentQuestion: QuizQuestion? = null,
    val selectedAnswer: String? = null,
    val isAnswerRevealed: Boolean = false,
    val quizScore: Int = 0,
    val quizTotal: Int = 0,

    // Quiz batch — hàng đợi 4 câu hỏi sau mỗi nhóm 4 thẻ
    val quizQueue: List<QuizQuestion> = emptyList(),
    val quizCurrentInBatch: Int = 0, // 0-based index trong quizQueue
    val quizBatchSize: Int = 0,      // tổng số câu trong batch hiện tại

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
