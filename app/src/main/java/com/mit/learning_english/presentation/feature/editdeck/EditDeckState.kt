package com.mit.learning_english.presentation.feature.editdeck

import android.net.Uri
import com.mit.learning_english.domain.model.FlashcardUpdateInput

import com.mit.learning_english.presentation.base.BaseUiState

data class EditDeckState(
    val deckId: Int = -1,
    val title: String = "",
    val coverImageUrl: String? = null,
    val coverImageUri: Uri? = null,
    val status: Int = 1,
    val flashcards: List<FlashcardUpdateInput> = emptyList(),
    val expandedIndex: Int = -1,
    override val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isUploadingImages: Boolean = false,
    override val errorMessage: String? = null
) : BaseUiState<EditDeckState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?) =
        copy(
            isLoading = isLoading ?: this.isLoading,
            errorMessage = errorMessage ?: this.errorMessage
        )
    val wordCount: Int get() = flashcards.filter { it.word.isNotBlank() && it.meaning.isNotBlank() }.size
    val maxWords: Int = 50
    val isAtLimit: Boolean get() = flashcards.size >= maxWords
}
