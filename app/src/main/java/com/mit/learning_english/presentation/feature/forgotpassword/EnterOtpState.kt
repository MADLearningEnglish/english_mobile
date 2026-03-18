package com.mit.learning_english.presentation.feature.forgotpassword

import com.mit.learning_english.presentation.base.BaseUiState

data class EnterOtpState(
    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
    val otp: String? = null,
    val email: String? = null,
    val serverError: String? = null
) : BaseUiState<EnterOtpState> {
    override fun copyWith(isLoading: Boolean?, errorMessage: String?) = copy(
        isLoading = isLoading ?: this.isLoading,
        errorMessage = errorMessage ?: this.errorMessage
    )

    fun copyWith(otp: String? = null, serverError: String? = null, email: String? = null) = this.copy(
        otp = otp ?: this.otp, serverError = serverError ?: this.serverError, email = email ?: this.email
    )
}
