package com.mit.learning_english.presentation.feature.decklist

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.deck.DeleteDeckUseCase
import com.mit.learning_english.domain.usecase.deck.GetAllDecksUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
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

    private var currentSearchQuery: String? = null

    fun loadDecks() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            val result = getAllDecksUseCase(currentSearchQuery)
            setLoading(false)
            when (result) {
                is Result.Success -> setState { copy(decks = result.data) }
                is Result.Error -> emitError(result.message ?: UiErrorKey.LOAD_DATA_VI)
                else -> Unit
            }
        }
    }

    fun onSearchTextChanged(query: String) {
        val trimmedQuery = query.trim().takeIf { it.isNotEmpty() }
        if (currentSearchQuery != trimmedQuery) {
            currentSearchQuery = trimmedQuery
            loadDecks()
        }
    }

    fun onDeckClick(deckId: Int, deckTitle: String) {
        emitEvent(DeckListEvent.ShowStudyModeDialog(deckId, deckTitle))
    }

    fun onStudyModeSelected(deckId: Int, deckTitle: String, isQuiz: Boolean) {
        if (isQuiz) {
            emitEvent(DeckListEvent.NavigateToQuiz(deckId, deckTitle))
        } else {
            emitEvent(DeckListEvent.NavigateToStudy(deckId, deckTitle))
        }
    }

    fun onMatchModeSelected(deckId: Int, deckTitle: String) {
        emitEvent(DeckListEvent.NavigateToMatch(deckId, deckTitle))
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
