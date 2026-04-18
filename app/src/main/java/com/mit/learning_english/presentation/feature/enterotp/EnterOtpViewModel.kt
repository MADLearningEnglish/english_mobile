package com.mit.learning_english.presentation.feature.enterotp

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.VerifyForgotPasswordOtpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EnterOtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyForgotPasswordOtpUseCase
) : BaseViewModel<EnterOtpState, EnterOtpEvent>(EnterOtpState()) {
    fun setOtp(otp: String) {
        setState { copy(otp = otp, serverError = null) }
    }

    fun setEmail(email: String) {
        setState { copy(email = email, serverError = null) }
    }

    fun onSubmitOtp() {
        viewModelScope.launch(exceptionHandler) {
            val otp = uiState.value.otp
            val email = uiState.value.email ?: ""
            if (otp.isNullOrEmpty() || email.isEmpty()) {
                setState { copy(serverError = UiErrorKey.OTP_OR_EMAIL_MISSING) }
                return@launch
            }

            setLoading(true)
            val result = verifyOtpUseCase(email, otp)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(EnterOtpEvent.NavigateToResetPassword)
                is Result.Error -> setState { copy(serverError = result.message) }
                else -> setState { copy(serverError = UiErrorKey.UNKNOWN) }
            }
        }
    }
}
