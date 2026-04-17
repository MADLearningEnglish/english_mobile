package com.mit.learning_english.presentation.feature.signup

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.SignUpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.shared.UiErrorKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<SignUpState, SignUpEvent>(SignUpState()) {
    fun setEmail(email: String) {
        setState { copy(serverError = null) }
    }

    fun setPassword(password: String) {
    }

    fun setFullName(fullName: String) {
    }

    fun onSignUpClick(email: String?, password: String?, fullName: String?) {
        viewModelScope.launch(exceptionHandler) {
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty() && !fullName.isNullOrEmpty()) {
                setLoading(true)
                val result = signUpUseCase(email, password, fullName)
                setLoading(false)
                if (result.isSuccess) {
                    if (result.getOrNull() == true) {
                        setState { copy(serverError = null) }
                        emitEvent(SignUpEvent.NavigateToLogin)
                    } else {
                        setState { copy(serverError = UiErrorKey.SIGNUP_FAILED) }
                    }
                } else if (result is Result.Error) {
                    setState { copy(serverError = result.message) }
                }
            }
        }
    }

}