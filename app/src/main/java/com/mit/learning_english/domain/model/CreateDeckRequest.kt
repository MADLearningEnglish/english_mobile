package com.mit.learning_english.domain.model

data class CreateDeckRequest(
    val title: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val flashcards: List<FlashcardInput>
)

data class FlashcardInput(
    val word: String = "",
    val phonetic: String = "",
    val meaning: String = "",
    val exampleSentence: String = "",
    val visualCueUrl: String? = null,
    val visualCueUri: android.net.Uri? = null,
    val note: String = ""
)
