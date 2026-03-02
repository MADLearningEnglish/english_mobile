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
                    if (result.getOrNull() == true) {
                        setState { copyWith(isSuccess = true, isLoading = false) }
                        emitEvent(LoginEvent.NavigateToHome)
                    } else {
                        setState { copyWith(isSuccess = false, isLoading = false) }
                    }
                } else if (result is Result.Error) {
                    setState { copyWith(errorMessage = result.message, isLoading = false) }
                }
            }
        }
    }

    fun setEmail(email: String) {
        setState {
            copyWith(email = email)
        }
    }

    fun setPassword(password: String) {
        setState {
            copyWith(password = password)
        }
    }

    fun onSignUpClick() {
        emitEvent(LoginEvent.NavigateToSignUp)
    }
}

