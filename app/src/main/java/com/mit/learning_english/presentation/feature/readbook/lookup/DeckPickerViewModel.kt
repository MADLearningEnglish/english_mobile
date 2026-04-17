package com.mit.learning_english.presentation.feature.readbook.lookup

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.domain.usecase.deck.GetAllDecksUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DeckPickerViewModel @Inject constructor(
    private val getAllDecksUseCase: GetAllDecksUseCase
) : BaseViewModel<DeckPickerUiState, Unit>(DeckPickerUiState()) {

    fun loadDecks() {
        viewModelScope.launch(exceptionHandler) {
            setState { copy(isLoading = true, errorMessage = null) }
            getAllDecksUseCase()
                .onSuccess { decks ->
                    setState {
                        copy(
                            isLoading = false,
                            decks = decks,
                            errorMessage = null
                        )
                    }
                }
                .onError { error ->
                    setState {
                        copy(
                            isLoading = false,
                            decks = emptyList(),
                            errorMessage = error.message
                        )
                    }
                }
        }
    }
}

data class DeckPickerUiState(
    val isLoading: Boolean = false,
    val decks: List<Deck> = emptyList(),
    val errorMessage: String? = null
)
