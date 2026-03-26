package com.mit.learning_english.presentation.feature.login

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.LoginUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginState, LoginEvent>(LoginState()) {
    fun onLoginClick() {
        viewModelScope.launch(exceptionHandler) {
            if (uiState.value.email != null && uiState.value.password != null) {
                setLoading(true)
                val result = loginUseCase(uiState.value.email!!, uiState.value.password!!)
                if (result.isSuccess) {
                    setLoading(false)
                    if (result.getOrNull() == true) {
                        setState { copy(isSuccess = true) }
                        emitEvent(LoginEvent.NavigateToHome)
                    } else {
                        setState { copy(isSuccess = false) }
                    }
                } else if (result is Result.Error) {
                    setLoading(false)
                    emitError(result.message ?: "Login failed")
                }
            }
        }
    }

    fun setEmail(email: String) {
        setState { copy(email = email) }
    }

    fun setPassword(password: String) {
        setState { copy(password = password) }
    }

    fun onSignUpClick() {
        emitEvent(LoginEvent.NavigateToSignUp)
    }
}

