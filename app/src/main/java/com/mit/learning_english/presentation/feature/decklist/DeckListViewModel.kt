package com.mit.learning_english.presentation.feature.decklist

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.deck.DeleteDeckUseCase
import com.mit.learning_english.domain.usecase.deck.GetAllDecksUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeckListViewModel @Inject constructor(
    private val getAllDecksUseCase: GetAllDecksUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase
) : BaseViewModel<DeckListState, DeckListEvent>(DeckListState()) {

    init {
        loadDecks()
    }

    fun loadDecks() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val result = getAllDecksUseCase()
            when (result) {
                is Result.Success -> setState {
                    copy(decks = result.data, isLoading = false, errorMessage = null)
                }
                is Result.Error -> setState {
                    copyWith(isLoading = false, errorMessage = result.message)
                }
                else -> setLoading(false)
            }
        }
    }

    fun onStartStudy(deckId: Int, deckTitle: String) {
        emitEvent(DeckListEvent.NavigateToStudy(deckId, deckTitle))
    }

    fun onEditDeck(deckId: Int) {
        emitEvent(DeckListEvent.NavigateToEditDeck(deckId))
    }

    fun onDeleteDeckRequest(deckId: Int, deckTitle: String) {
        emitEvent(DeckListEvent.ShowDeleteConfirmDialog(deckId, deckTitle))
    }

    fun onConfirmDelete(deckId: Int) {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val result = deleteDeckUseCase(deckId)
            when (result) {
                is Result.Success -> {
                    setState {
                        copy(
                            decks = decks.filter { it.id != deckId },
                            isLoading = false
                        )
                    }
                    emitEvent(DeckListEvent.ShowSnackbar("Xóa bộ thẻ thành công"))
                }
                is Result.Error -> {
                    setState { copyWith(isLoading = false) }
                    emitEvent(DeckListEvent.ShowSnackbar("Xóa thất bại, thử lại sau"))
                }
                else -> setLoading(false)
            }
        }
    }

    fun onCreateDeck() {
        emitEvent(DeckListEvent.NavigateToCreateDeck)
    }
}
