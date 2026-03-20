package com.mit.learning_english.presentation.feature.readbook

import com.mit.learning_english.domain.model.Page
import com.mit.learning_english.presentation.base.BaseUiState

data class ReadBookState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val isPlaying: Boolean,
    val currentTime: Int,
    val readMode: ReadMode,
    val page: Page
) : BaseUiState<ReadBookState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): ReadBookState {
        return this.copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }

}