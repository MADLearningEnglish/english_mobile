package com.mit.learning_english.presentation.feature.study

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.deck.GetStudyFlashcardsUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

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

    private fun loadFlashcards() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            when (val result = getStudyFlashcardsUseCase(deckId)) {
                is Result.Success -> setState {
                    copy(flashcards = result.data, isLoading = false, currentIndex = 0, isFlipped = false)
                }
                is Result.Error -> setState {
                    copyWith(isLoading = false, errorMessage = result.message)
                }
                else -> setLoading(false)
            }
        }
    }

    fun flipCard() {
        setState { copy(isFlipped = !isFlipped) }
    }

    fun onNextCard() {
        val state = uiState.value
        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.totalCount) {
            setState { copy(isComplete = true) }
            emitEvent(StudyEvent.SessionComplete)
        } else {
            setState { copy(currentIndex = nextIndex, isFlipped = false) }
        }
    }

    fun onNavigateBack() {
        emitEvent(StudyEvent.NavigateBack)
    }
}
