package com.mit.learning_english.presentation.feature.forgotpassword

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.ResetForgotPasswordUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetUseCase: ResetForgotPasswordUseCase
) : BaseViewModel<ResetPasswordState, ResetPasswordEvent>(ResetPasswordState()) {

    fun setPassword(password: String) {
        setState { copyWith(password = password, serverError = null) }
    }

    fun setRePassword(rePassword: String) {
        setState { copyWith(rePassword = rePassword, serverError = null) }
    }

    fun setEmail(email: String) {
        setState { copyWith(email = email, serverError = null) }
    }

    fun setOtp(otp: String) {
        setState { copyWith(otp = otp, serverError = null) }
    }

    fun onUpdatePassword() {
        viewModelScope.launch(exceptionHandler) {
            val pwd = uiState.value.password
            val rePwd = uiState.value.rePassword
            val email = uiState.value.email ?: ""
            val otp = uiState.value.otp ?: ""

            if (pwd.isNullOrEmpty() || rePwd.isNullOrEmpty() || pwd != rePwd) {
                setState { copyWith(serverError = "Password mismatch or empty") }
                return@launch
            }

            setLoading(true)
            val result = resetUseCase(email, otp, pwd)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(ResetPasswordEvent.NavigateToLogin)
                is Result.Error -> setState { copyWith(serverError = result.message) }
                else -> setState { copyWith(serverError = "Unknown error") }
            }
        }
    }
}
