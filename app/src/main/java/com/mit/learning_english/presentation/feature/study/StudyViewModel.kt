package com.mit.learning_english.presentation.feature.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.QuizQuestion
import com.mit.learning_english.domain.model.QuizType
import com.mit.learning_english.domain.usecase.deck.GetStudyFlashcardsUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Cứ mỗi QUIZ_INTERVAL thẻ sẽ xuất hiện 1 câu hỏi quiz */
private const val QUIZ_INTERVAL = 4

@HiltViewModel
class StudyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStudyFlashcardsUseCase: GetStudyFlashcardsUseCase
) : BaseViewModel<StudyState, StudyEvent>(StudyState()) {

    private val deckId: Int = savedStateHandle.get<Int>("deckId") ?: 0
    private val deckTitleArg: String = savedStateHandle.get<String>("deckTitle") ?: ""

    init {
        setState { copy(deckTitle = deckTitleArg) }
        loadFlashcards()
    }

    // =================== Load ===================

    private fun loadFlashcards() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            when (val result = getStudyFlashcardsUseCase(deckId)) {
                is Result.Success -> setState {
                    copy(
                        flashcards = result.data,
                        isLoading = false,
                        currentIndex = 0,
                        isFlipped = false
                    )
                }
                is Result.Error -> setState {
                    copyWith(isLoading = false, errorMessage = result.message)
                }
                else -> setLoading(false)
            }
        }
    }

    // =================== Flashcard actions ===================

    fun flipCard() {
        setState { copy(isFlipped = !isFlipped) }
    }

    /**
     * Tiến đến bước tiếp theo:
     * - Nếu (currentIndex + 1) % QUIZ_INTERVAL == 0 → sinh batch 4 câu quiz (ngẫu nhiên thứ tự)
     * - Nếu hết bộ thẻ → kết thúc phiên học
     * - Còn lại → hiện thẻ tiếp theo
     */
    fun onNextCard() {
        val state = uiState.value
        val nextIndex = state.currentIndex + 1

        val shouldShowQuiz = nextIndex % QUIZ_INTERVAL == 0 && nextIndex < state.totalCount
        if (shouldShowQuiz) {
            // Lấy 4 thẻ vừa học (nhóm hiện tại)
            val groupStart = nextIndex - QUIZ_INTERVAL
            val groupCards = state.flashcards.subList(groupStart, nextIndex)

            // Sinh 1 quiz cho mỗi thẻ trong nhóm, shuffle thứ tự
            val batch = groupCards
                .mapNotNull { card -> generateQuizQuestion(state.flashcards, card) }
                .shuffled()

            if (batch.isNotEmpty()) {
                setState {
                    copy(
                        currentIndex = nextIndex,
                        isFlipped = false,
                        studyMode = StudyMode.QUIZ,
                        quizQueue = batch,
                        quizCurrentInBatch = 0,
                        quizBatchSize = batch.size,
                        currentQuestion = batch[0],
                        selectedAnswer = null,
                        isAnswerRevealed = false,
                        quizTotal = quizTotal + batch.size
                    )
                }
                return
            }
        }

        if (nextIndex >= state.totalCount) {
            setState { copy(isComplete = true) }
            emitEvent(StudyEvent.SessionComplete)
        } else {
            setState { copy(currentIndex = nextIndex, isFlipped = false) }
        }
    }

    // =================== Quiz actions ===================

    /** Người dùng chọn đáp án. Chỉ được chọn 1 lần. */
    fun onAnswerSelected(answer: String) {
        val state = uiState.value
        if (state.isAnswerRevealed) return
        val isCorrect = answer == state.currentQuestion?.correctAnswer
        setState {
            copy(
                selectedAnswer = answer,
                isAnswerRevealed = true,
                quizScore = if (isCorrect) quizScore + 1 else quizScore
            )
        }
    }

    /**
     * Xử lý FILL_BLANK: người dùng nhập text và submit.
     */
    fun onFillBlankSubmit(input: String) {
        val state = uiState.value
        if (state.isAnswerRevealed) return
        val correct = state.currentQuestion?.correctAnswer ?: return
        val isCorrect = input.trim().equals(correct.trim(), ignoreCase = true)
        setState {
            copy(
                selectedAnswer = input.trim(),
                isAnswerRevealed = true,
                quizScore = if (isCorrect) quizScore + 1 else quizScore
            )
        }
    }

    /**
     * Tiếp tục câu quiz tiếp theo trong batch.
     * Nếu hết batch → quay về FLASHCARD.
     */
    fun onQuizNext() {
        val state = uiState.value
        val nextInBatch = state.quizCurrentInBatch + 1

        if (nextInBatch < state.quizQueue.size) {
            // Còn câu trong batch → hiện câu tiếp theo
            setState {
                copy(
                    quizCurrentInBatch = nextInBatch,
                    currentQuestion = quizQueue[nextInBatch],
                    selectedAnswer = null,
                    isAnswerRevealed = false
                )
            }
        } else {
            // Hết batch → kiểm tra xem còn thẻ tiếp theo không
            if (state.currentIndex >= state.totalCount) {
                setState { copy(isComplete = true) }
                emitEvent(StudyEvent.SessionComplete)
            } else {
                setState {
                    copy(
                        studyMode = StudyMode.FLASHCARD,
                        quizQueue = emptyList(),
                        quizCurrentInBatch = 0,
                        quizBatchSize = 0,
                        currentQuestion = null,
                        selectedAnswer = null,
                        isAnswerRevealed = false,
                        isFlipped = false
                    )
                }
            }
        }
    }

    fun onNavigateBack() {
        emitEvent(StudyEvent.NavigateBack)
    }

    // =================== Quiz generation ===================

    /**
     * Sinh 1 câu hỏi quiz từ [target] sử dụng các thẻ khác trong [all] làm đáp án nhiễu.
     * Trả về null nếu bộ thẻ có < 4 thẻ (không đủ đáp án nhiễu).
     */
    private fun generateQuizQuestion(all: List<Flashcard>, target: Flashcard): QuizQuestion? {
        if (all.size < 4) return null

        val distractors = all
            .filter { it.id != target.id }
            .shuffled()
            .take(3)

        // Ưu tiên FILL_BLANK nếu có exampleSentence
        val candidateTypes = mutableListOf(QuizType.MEANING_TO_WORD, QuizType.WORD_TO_MEANING)
        if (!target.exampleSentence.isNullOrBlank()) {
            candidateTypes.add(QuizType.FILL_BLANK)
        }
        val type = candidateTypes.random()

        return when (type) {
            QuizType.MEANING_TO_WORD -> {
                val choices = (distractors.map { it.word } + target.word).shuffled()
                QuizQuestion(
                    type = QuizType.MEANING_TO_WORD,
                    prompt = target.meaning,
                    correctAnswer = target.word,
                    choices = choices,
                    sourceFlashcardId = target.id
                )
            }
            QuizType.WORD_TO_MEANING -> {
                val choices = (distractors.map { it.meaning } + target.meaning).shuffled()
                QuizQuestion(
                    type = QuizType.WORD_TO_MEANING,
                    prompt = target.word,
                    correctAnswer = target.meaning,
                    choices = choices,
                    sourceFlashcardId = target.id
                )
            }
            QuizType.FILL_BLANK -> {
                val sentence = target.exampleSentence!!
                val blanked = sentence.replace(
                    target.word,
                    "_____",
                    ignoreCase = true
                )
                QuizQuestion(
                    type = QuizType.FILL_BLANK,
                    prompt = blanked,
                    correctAnswer = target.word,
                    choices = emptyList(),   // không cần choices
                    sourceFlashcardId = target.id
                )
            }
        }
    }
}
