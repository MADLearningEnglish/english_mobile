package com.mit.learning_english.presentation.feature.resetpassword

import com.mit.learning_english.presentation.base.BaseUiState

data class ResetPasswordState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val password: String? = null,
    val rePassword: String? = null,
    val email: String? = null,
    val otp: String? = null,
    val serverError: String? = null
) : BaseUiState<ResetPasswordState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?) = copy(
        isLoading = isLoading ?: this.isLoading, errorMessage = errorMessage ?: this.errorMessage
    )

    fun copyWith(
        password: String? = null,
        rePassword: String? = null,
        serverError: String? = null,
        email: String? = null,
        otp: String? = null
    ) = this.copy(
        password = password ?: this.password,
        rePassword = rePassword ?: this.rePassword,
        serverError = serverError ?: this.serverError,
        email = email ?: this.email,
        otp = otp ?: this.otp
    )
}
