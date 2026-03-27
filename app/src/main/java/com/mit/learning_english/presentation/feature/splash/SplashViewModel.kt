package com.mit.learning_english.presentation.feature.splash

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.CheckLoggedInUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkLoggedInUseCase: CheckLoggedInUseCase
) : BaseViewModel<Unit, SplashEvent>(Unit) {
    fun checkAndNavigate() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            delay(500)
            if (checkLoggedInUseCase()) {
                emitEvent(SplashEvent.NavigateToHome)
            } else {
                emitEvent(SplashEvent.NavigateToLogin)
            }
            setLoading(false)
        }
    }
}