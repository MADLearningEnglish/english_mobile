package com.mit.learning_english.domain.model

data class Deck(
    val id: Int,
    val title: String,
    val status: Int = 1,
    val flashcards: List<Flashcard> = emptyList()
)