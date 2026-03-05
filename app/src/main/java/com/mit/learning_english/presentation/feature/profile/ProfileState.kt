package com.mit.learning_english.presentation.feature.profile

import com.mit.learning_english.presentation.base.BaseUiState

data class ProfileState(
    override val isLoading: Boolean = false, override val errorMessage: String? = null
) : BaseUiState<ProfileState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): ProfileState {
        return copy(isLoading = isLoading ?: this.isLoading, errorMessage ?: this.errorMessage)
    }
}
