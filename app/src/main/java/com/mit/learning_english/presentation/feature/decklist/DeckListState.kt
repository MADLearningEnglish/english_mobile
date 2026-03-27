package com.mit.learning_english.presentation.feature.decklist

import com.mit.learning_english.domain.model.Deck

data class DeckListState(
    val decks: List<Deck> = emptyList(),
)
