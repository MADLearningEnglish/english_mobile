package com.mit.learning_english.domain.model

data class CreateDeckRequest(
    val title: String,
    val flashcards: List<FlashcardInput>
)

data class FlashcardInput(
    val term: String = "",
    val definition: String = "",
    val imageUrl: String? = null,
    val imageUri: android.net.Uri? = null
)
