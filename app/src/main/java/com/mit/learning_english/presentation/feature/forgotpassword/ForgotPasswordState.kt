package com.mit.learning_english.presentation.feature.forgotpassword

import com.mit.learning_english.presentation.base.BaseUiState

data class ForgotPasswordState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val email: String? = null,
    val serverError: String? = null,
) : BaseUiState<ForgotPasswordState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?) = copy(
        isLoading = isLoading ?: this.isLoading,
        errorMessage = errorMessage ?: this.errorMessage
    )

    fun copyWith(email: String? = null, serverError: String? = null): ForgotPasswordState {
        return this.copy(email = email ?: this.email, serverError = serverError ?: this.serverError)
    }
}
