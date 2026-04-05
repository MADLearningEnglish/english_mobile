package com.mit.learning_english.presentation.feature.createdeck

import com.mit.learning_english.domain.model.FlashcardInput

data class CreateDeckState(
    val title: String = "",
    val description: String = "",
    val coverImageUri: android.net.Uri? = null,
    val coverImageUrl: String? = null,
    val isUploadingImages: Boolean = false,
    val flashcards: List<FlashcardInput> = listOf(FlashcardInput()),
    val expandedIndex: Int = 0,
    val isSaving: Boolean = false,
) {
    val wordCount: Int get() = flashcards.size
    val maxWords: Int = 35
    val isAtLimit: Boolean get() = wordCount >= maxWords
    val progressFraction: Float get() = wordCount.toFloat() / maxWords.toFloat()
}
