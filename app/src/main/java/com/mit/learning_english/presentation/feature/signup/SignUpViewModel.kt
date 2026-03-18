package com.mit.learning_english.presentation.feature.signup

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.SignUpUseCase
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : BaseViewModel<SignUpState, SignUpEvent>(SignUpState()) {

    fun setEmail(email: String) {
        setState { copyWith(errorMessage = null) }
        setState { copyWith() } // keep placeholder pattern
        setState { copyWith() }
        setState { copyWith() }
        setState { copyWith() }
        setState { copyWith() }
        // store email in state by extending state object if needed
        // For now we keep sign-up inputs in local variables via ViewModel scope
        setState { copyWith() }
    }

    fun setPassword(password: String) {
        // no-op placeholder to match pattern; state currently only tracks loading and errorMessage
    }

    fun setFullName(fullName: String) {
        // no-op placeholder
    }

    fun onSignUpClick(email: String?, password: String?, fullName: String?) {
        viewModelScope.launch(exceptionHandler) {
            if (!email.isNullOrEmpty() && !password.isNullOrEmpty() && !fullName.isNullOrEmpty()) {
                setLoading(true)
                val result = signUpUseCase(email, password, fullName)
                if (result.isSuccess) {
                    // treat success boolean true as success
                    if (result.getOrNull() == true) {
                        setState { copyWith(isLoading = false, errorMessage = null) }
                        // emit navigation event to go back to login screen
                        emitEvent(SignUpEvent.NavigateToLogin)
                    } else {
                        setState { copyWith(isLoading = false, errorMessage = "Sign up failed") }
                    }
                } else if (result is Result.Error) {
                    setState { copyWith(errorMessage = result.message, isLoading = false) }
                }
            }
        }
    }

}