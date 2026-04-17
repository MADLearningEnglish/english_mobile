package com.mit.learning_english.presentation.feature.resetpassword

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.ResetForgotPasswordUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetUseCase: ResetForgotPasswordUseCase
) : BaseViewModel<ResetPasswordState, ResetPasswordEvent>(ResetPasswordState()) {
    fun setPassword(password: String) {
        setState { copy(password = password, serverError = null) }
    }

    fun setRePassword(rePassword: String) {
        setState { copy(rePassword = rePassword, serverError = null) }
    }

    fun setEmail(email: String) {
        setState { copy(email = email, serverError = null) }
    }

    fun setOtp(otp: String) {
        setState { copy(otp = otp, serverError = null) }
    }

    fun onUpdatePassword() {
        viewModelScope.launch(exceptionHandler) {
            val pwd = uiState.value.password
            val rePwd = uiState.value.rePassword
            val email = uiState.value.email ?: ""
            val otp = uiState.value.otp ?: ""

            if (pwd.isNullOrEmpty() || rePwd.isNullOrEmpty() || pwd != rePwd) {
                setState { copy(serverError = UiErrorKey.PASSWORD_MISMATCH_OR_EMPTY) }
                return@launch
            }

            setLoading(true)
            val result = resetUseCase(email, otp, pwd)
            setLoading(false)

            when (result) {
                is Result.Success -> emitEvent(ResetPasswordEvent.NavigateToLogin)
                is Result.Error -> setState { copy(serverError = result.message) }
                else -> setState { copy(serverError = UiErrorKey.UNKNOWN) }
            }
        }
    }
}
