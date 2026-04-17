package com.mit.learning_english.presentation.feature.study.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.model.QuizQuestion
import com.mit.learning_english.domain.model.QuizType
import com.mit.learning_english.domain.usecase.deck.GetStudyFlashCardsUseCase
import com.mit.learning_english.domain.usecase.deck.LogDeckStudyCompleteUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStudyFlashcardsUseCase: GetStudyFlashCardsUseCase,
    private val logDeckStudyCompleteUseCase: LogDeckStudyCompleteUseCase
) : BaseViewModel<QuizState, QuizEvent>(QuizState()) {

    private val deckId: Int = savedStateHandle.get<Int>("deckId") ?: 0
    private var sessionStartMs: Long? = null
    private val deckTitleArg: String = savedStateHandle.get<String>("deckTitle") ?: ""

    init {
        setState { copy(deckTitle = deckTitleArg) }
        loadAndGenerateQuiz()
    }

    private fun loadAndGenerateQuiz() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            when (val result = getStudyFlashcardsUseCase(deckId)) {
                is Result.Success -> {
                    setLoading(false)
                    sessionStartMs = System.currentTimeMillis()
                    
                    val flashcards = result.data
                    val questions = flashcards.mapNotNull { card -> generateQuizQuestion(flashcards, card) }.shuffled()
                    
                    setState {
                        copy(
                            flashcards = flashcards,
                            quizQueue = questions,
                            currentIndex = 0,
                            totalCount = questions.size
                        )
                    }
                }
                is Result.Error -> {
                    setLoading(false)
                    emitError(result.message ?: "Lỗi tải dữ liệu")
                }
                else -> setLoading(false)
            }
        }
    }

    fun onAnswerSelected(answer: String) {
        val state = uiState.value
        if (state.isAnswerRevealed || state.isComplete) return
        
        val correct = state.currentQuestion?.correctAnswer ?: return
        val isCorrect = answer == correct
        
        setState {
            copy(
                selectedAnswer = answer,
                isAnswerRevealed = true,
                correctCount = if (isCorrect) correctCount + 1 else correctCount
            )
        }
    }

    fun onNextQuestion() {
        val state = uiState.value
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= state.totalCount) {
            setState { copy(isComplete = true) }
            notifySessionComplete(state)
        } else {
            setState {
                copy(
                    currentIndex = nextIndex,
                    selectedAnswer = null,
                    isAnswerRevealed = false
                )
            }
        }
    }

    fun onNavigateBack() {
        emitEvent(QuizEvent.NavigateBack)
    }

    private fun notifySessionComplete(state: QuizState) {
        emitEvent(QuizEvent.SessionComplete)
        val start = sessionStartMs
        if (start == null || deckId <= 0 || state.totalCount <= 0) return
        val durationSec = ((System.currentTimeMillis() - start) / 1000).toInt().coerceAtLeast(1)
        viewModelScope.launch(exceptionHandler) {
            logDeckStudyCompleteUseCase(
                deckId = deckId,
                durationSeconds = durationSec,
                cardsReviewed = 0, // Not a flashcard session
                quizCorrect = state.correctCount,
                quizTotal = state.totalCount
            )
        }
    }

    private fun generateQuizQuestion(all: List<Flashcard>, target: Flashcard): QuizQuestion? {
        if (all.isEmpty()) return null

        val distractors = all
            .filter { it.id != target.id }
            .shuffled()
            .take(3)

        // The user's image shows "Nghĩa -> Lựa chọn từ" (Definition -> Word)
        // Let's stick with that for Quiz Mode as requested implicitly by the image.
        val choices = (distractors.map { it.term } + target.term).shuffled()
        
        // We will pass the imageUrl through prompt secondary or we handle it in fragment.
        // Actually, QuizQuestion doesn't have an imageUrl field, but we have sourceFlashcardId.
        // We can fetch imageUrl from the list of all flashcards in state... Wait, we don't store flashcards in QuizState.
        // Let's pass the imageUrl by including a new field to QuizQuestion? 
        // Wait! The user's image shows the Definition + Term combined? 
        // Image shows: "/'bæg.ɪdʒ/ - hành lý" and an image. And the options are: "suitcase (n), baggage (n), author (n), service (n)".
        // Meaning: the prompt is the Definition, and the choices are the Terms. 
        return QuizQuestion(
            type = QuizType.MEANING_TO_WORD,
            prompt = target.definition, 
            correctAnswer = target.term,
            choices = choices,
            sourceFlashcardId = target.id
        )
    }
}
