package com.mit.learning_english.presentation.feature.decklist

import com.mit.learning_english.domain.model.Deck
import com.mit.learning_english.presentation.base.BaseUiState

data class DeckListState(
    val decks: List<Deck> = emptyList(),
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null
) : BaseUiState<DeckListState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?) = copy(
        isLoading = isLoading ?: this.isLoading,
        errorMessage = errorMessage
    )
}
