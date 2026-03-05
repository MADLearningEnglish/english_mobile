package com.mit.learning_english.presentation.feature.chat

import com.mit.learning_english.presentation.base.BaseUiState

data class ChatState(
    override val isLoading: Boolean = false, override val errorMessage: String? = null
) : BaseUiState<ChatState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): ChatState {
        return copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }
}
