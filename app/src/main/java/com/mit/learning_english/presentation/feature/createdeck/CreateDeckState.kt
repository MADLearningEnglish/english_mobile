package com.mit.learning_english.presentation.feature.createdeck

import com.mit.learning_english.domain.model.FlashcardInput
import com.mit.learning_english.presentation.base.BaseUiState

data class CreateDeckState(
    val title: String = "",
    val description: String = "",
    val coverImageUri: android.net.Uri? = null,
    val coverImageUrl: String? = null,
    val isUploadingImages: Boolean = false,
    val flashcards: List<FlashcardInput> = listOf(FlashcardInput()),
    val expandedIndex: Int = 0,
    val isSaving: Boolean = false,
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null
) : BaseUiState<CreateDeckState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?) =
        copy(
            isLoading = isLoading ?: this.isLoading,
            errorMessage = errorMessage ?: this.errorMessage
        )

    val wordCount: Int get() = flashcards.size
    val maxWords: Int = 35
    val isAtLimit: Boolean get() = wordCount >= maxWords
    val progressFraction: Float get() = wordCount.toFloat() / maxWords.toFloat()
}
