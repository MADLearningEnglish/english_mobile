package com.mit.learning_english.domain.model

data class UpdateDeckRequest(
    val title: String,
    val status: Int,
    val flashcards: List<FlashcardUpdateInput>
)

data class FlashcardUpdateInput(
    val id: Int? = null,
    val term: String = "",
    val definition: String = "",
    val imageUrl: String? = null,
    val imageUri: android.net.Uri? = null,
    val status: Int? = null
)
