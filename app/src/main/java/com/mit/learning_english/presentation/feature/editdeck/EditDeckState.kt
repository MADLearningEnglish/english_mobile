package com.mit.learning_english.presentation.feature.editdeck

import com.mit.learning_english.domain.model.FlashcardUpdateInput

data class EditDeckState(
    val deckId: Int = -1,
    val title: String = "",

    val status: Int = 1,
    val flashcards: List<FlashcardUpdateInput> = emptyList(),
    val expandedIndex: Int = -1,
    val isSaving: Boolean = false,
    val isUploadingImages: Boolean = false,
) {
    val wordCount: Int get() = flashcards.filter { it.term.isNotBlank() && it.definition.isNotBlank() }.size
    val maxWords: Int = 50
    val isAtLimit: Boolean get() = flashcards.size >= maxWords
}
