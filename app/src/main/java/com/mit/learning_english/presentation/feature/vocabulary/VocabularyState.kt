package com.mit.learning_english.presentation.feature.vocabulary

import com.mit.learning_english.presentation.base.BaseUiState

data class VocabularyState(
    override val isLoading: Boolean = false, override val errorMessage: String? = null
) : BaseUiState<VocabularyState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): VocabularyState {
        return copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }
}
