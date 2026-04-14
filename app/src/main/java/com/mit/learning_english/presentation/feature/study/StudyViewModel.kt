package com.mit.learning_english.presentation.feature.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.Flashcard
import com.mit.learning_english.domain.usecase.deck.GetStudyFlashCardsUseCase
import com.mit.learning_english.domain.usecase.deck.LogDeckStudyCompleteUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getStudyFlashcardsUseCase: GetStudyFlashCardsUseCase,
    private val logDeckStudyCompleteUseCase: LogDeckStudyCompleteUseCase
) : BaseViewModel<StudyState, StudyEvent>(StudyState()) {

    private val deckId: Int = savedStateHandle.get<Int>("deckId") ?: 0
    private var sessionStartMs: Long? = null
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
                is Result.Success -> {
                    setLoading(false)
                    sessionStartMs = System.currentTimeMillis()
                    setState {
                        copy(
                            flashcards = result.data,
                            currentIndex = 0,
                            isFlipped = false
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

    // =================== Flashcard actions hdhdhdhjhdjhjfhfhjhjf===================

    fun flipCard() {
        setState { copy(isFlipped = !isFlipped) }
    }

    fun onPreviousCard() {
        val state = uiState.value
        if (state.currentIndex > 0) {
            setState { copy(currentIndex = currentIndex - 1, isFlipped = false) }
        }
    }

    fun onNextCard() {
        val state = uiState.value
        val nextIndex = state.currentIndex + 1

        if (nextIndex >= state.totalCount) {
            setState { copy(isComplete = true) }
            notifySessionComplete(state)
        } else {
            setState { copy(currentIndex = nextIndex, isFlipped = false) }
        }
    }



    fun onNavigateBack() {
        emitEvent(StudyEvent.NavigateBack)
    }

    private fun notifySessionComplete(state: StudyState) {
        emitEvent(StudyEvent.SessionComplete)
        val start = sessionStartMs
        if (start == null || deckId <= 0 || state.totalCount <= 0) return
        val durationSec =
            ((System.currentTimeMillis() - start) / 1000).toInt().coerceAtLeast(1)
        viewModelScope.launch(exceptionHandler) {
            logDeckStudyCompleteUseCase(
                deckId = deckId,
                durationSeconds = durationSec,
                cardsReviewed = state.totalCount,
                quizCorrect = null,
                quizTotal = null
               
            )
        }
    }


}
