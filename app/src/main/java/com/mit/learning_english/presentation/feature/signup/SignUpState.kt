package com.mit.learning_english.presentation.feature.signup

import com.mit.learning_english.presentation.base.BaseUiState

data class SignUpState(
    override val isLoading: Boolean = false, override val errorMessage: String? = null
) : BaseUiState<SignUpState> {
    override fun copyWith(
        isLoading: Boolean?, errorMessage: String?
    ): SignUpState {
        return this.copy(
            isLoading ?: this.isLoading, errorMessage ?: this.errorMessage
        )
    }
}