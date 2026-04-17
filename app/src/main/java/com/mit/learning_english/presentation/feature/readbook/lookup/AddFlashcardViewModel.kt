package com.mit.learning_english.presentation.feature.readbook.lookup

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.FlashcardUpdateInput
import com.mit.learning_english.domain.model.UpdateDeckRequest
import com.mit.learning_english.domain.usecase.deck.GetDeckByIdUseCase
import com.mit.learning_english.domain.usecase.deck.UpdateDeckUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class AddFlashcardViewModel @Inject constructor(
    private val getDeckByIdUseCase: GetDeckByIdUseCase,
    private val updateDeckUseCase: UpdateDeckUseCase
) : BaseViewModel<AddFlashcardUiState, Unit>(AddFlashcardUiState()) {

    fun consumeError() {
        setState { copy(errorMessage = null) }
    }

    fun submit(deckId: Int, term: String, definition: String) {
        val normalizedTerm = term.trim()
        val normalizedDefinition = definition.trim()
        if (normalizedTerm.isBlank() || normalizedDefinition.isBlank()) {
            setState { copy(errorMessage = "Please fill in term and definition.") }
            return
        }

        viewModelScope.launch(exceptionHandler) {
            setState { copy(isSubmitting = true, errorMessage = null) }
            getDeckByIdUseCase(deckId)
                .onSuccess { deck ->
                    val existingInputs = deck.flashcards.map { card ->
                        FlashcardUpdateInput(
                            id = card.id,
                            term = card.term,
                            definition = card.definition,
                            imageUrl = card.imageUrl,
                            status = 1
                        )
                    }
                    val request = UpdateDeckRequest(
                        title = deck.title,
                        status = deck.status,
                        flashcards = existingInputs + FlashcardUpdateInput(
                            term = normalizedTerm,
                            definition = normalizedDefinition,
                            status = 1
                        )
                    )
                    updateDeckUseCase(deckId, request)
                        .onSuccess {
                            setState { copy(isSubmitting = false, isSuccess = true, errorMessage = null) }
                        }
                        .onError { error ->
                            setState { copy(isSubmitting = false, errorMessage = error.message) }
                        }
                }
                .onError { error ->
                    setState { copy(isSubmitting = false, errorMessage = error.message) }
                }
        }
    }
}

data class AddFlashcardUiState(
    val isSubmitting: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)
