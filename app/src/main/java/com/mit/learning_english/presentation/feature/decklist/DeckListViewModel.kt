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
            setLoading(false)
            when (result) {
                is Result.Success -> setState { copy(decks = result.data) }
                is Result.Error -> emitError(result.message ?: "Lỗi tải dữ liệu")
                else -> Unit
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
                    setLoading(false)
                    setState { copy(decks = decks.filter { it.id != deckId }) }
                    emitEvent(DeckListEvent.ShowSnackbar("Xóa bộ thẻ thành công"))
                }
                is Result.Error -> {
                    setLoading(false)
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
