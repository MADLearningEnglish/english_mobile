package com.mit.learning_english.domain.model

data class Deck(
    val id: Int,
    val title: String,
    val description: String? = null,
    val coverImageUrl: String?,
    val totalWords: Int,
    val status: Int = 1,
    val flashcards: List<Flashcard> = emptyList()
)