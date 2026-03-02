package com.mit.learning_english.presentation.feature.login

import com.mit.learning_english.presentation.base.BaseUiState

data class LoginState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val email: String? = null,
    val password: String? = null,
    val isSuccess: Boolean? = null,
) : BaseUiState<LoginState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?): LoginState = copy(
        isLoading = isLoading ?: this.isLoading, errorMessage = errorMessage ?: this.errorMessage
    )

    fun copyWith(
        isLoading: Boolean? = null,
        errorMessage: String? = null,
        email: String? = null,
        password: String? = null,
        isSuccess: Boolean? = null,
    ): LoginState {
        return this.copy(
            isLoading = isLoading ?: this.isLoading,
            errorMessage = errorMessage ?: this.errorMessage,
            email = email ?: this.email,
            password = password ?: this.password,
            isSuccess = isSuccess ?: this.isSuccess
        )
    }
}
