package com.mit.learning_english.presentation.feature.decklist

sealed class DeckListEvent {
    data class NavigateToStudy(val deckId: Int, val deckTitle: String) : DeckListEvent()
    data class NavigateToEditDeck(val deckId: Int) : DeckListEvent()
    object NavigateToCreateDeck : DeckListEvent()
    data class ShowDeleteConfirmDialog(val deckId: Int, val deckTitle: String) : DeckListEvent()
    data class ShowSnackbar(val message: String) : DeckListEvent()
}
