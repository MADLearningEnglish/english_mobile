package com.mit.learning_english.domain.model

data class UpdateDeckRequest(
    val title: String,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val status: Int,
    val flashcards: List<FlashcardUpdateInput>
)

data class FlashcardUpdateInput(
    val id: Int? = null,
    val word: String = "",
    val phonetic: String = "",
    val meaning: String = "",
    val exampleSentence: String = "",
    val visualCueUrl: String? = null,
    val visualCueUri: android.net.Uri? = null,
    val note: String = "",
    val status: Int? = null
)
