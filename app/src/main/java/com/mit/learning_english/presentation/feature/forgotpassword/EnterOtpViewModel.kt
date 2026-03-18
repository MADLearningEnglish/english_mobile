package com.mit.learning_english.presentation.feature.forgotpassword

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.VerifyForgotPasswordOtpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyForgotPasswordOtpUseCase
) : BaseViewModel<EnterOtpState, EnterOtpEvent>(EnterOtpState()) {

    fun setOtp(otp: String) {
        setState { copyWith(otp = otp, serverError = null) }
    }

    fun setEmail(email: String) {
        setState { copyWith(email = email, serverError = null) }
    }

    fun onSubmitOtp() {
        viewModelScope.launch(exceptionHandler) {
            val otp = uiState.value.otp
            // We expect the email to be provided via navigation args or a shared ViewModel.
            // For simplicity, we assume repository/UseCase will use stored email or the app passes email via saved state.
            val email = uiState.value.email ?: ""
            if (otp.isNullOrEmpty() || email.isEmpty()) {
                setState { copyWith(serverError = "OTP or email missing") }
                return@launch
            }

            setLoading(true)
            val result = verifyOtpUseCase(email, otp)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(EnterOtpEvent.NavigateToResetPassword)
                is Result.Error -> setState { copyWith(serverError = result.message) }
                else -> setState { copyWith(serverError = "Unknown error") }
            }
        }
    }
}
