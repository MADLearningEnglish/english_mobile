package com.mit.learning_english.presentation.feature.forgotpassword

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.RequestForgotPasswordOtpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val requestOtpUseCase: RequestForgotPasswordOtpUseCase
) : BaseViewModel<ForgotPasswordState, ForgotPasswordEvent>(ForgotPasswordState()) {
    fun setEmail(email: String) {
        setState { copy(email = email, serverError = null) }
    }

    fun onRequestOtp() {
        viewModelScope.launch(exceptionHandler) {
            val email = uiState.value.email
            if (email.isNullOrEmpty()) {
                setState { copy(serverError = "Email is required") }
                return@launch
            }

            setLoading(true)
            val result = requestOtpUseCase(email)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(ForgotPasswordEvent.NavigateToEnterOtp)
                is Result.Error -> setState { copy(serverError = result.message) }
                else -> setState { copy(serverError = "Unknown error") }
            }
        }
    }
}
