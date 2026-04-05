package com.mit.learning_english.presentation.feature.splash

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.auth.CheckLoggedInUseCase
import com.mit.learning_english.domain.usecase.onboarding.CheckSeenOnboardingAfterLoginUseCase
import com.mit.learning_english.domain.usecase.onboarding.CheckSeenOnboardingBeforeLoginUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkLoggedInUseCase: CheckLoggedInUseCase,
    private val hasSeenOnboardingBeforeLoginUseCase: CheckSeenOnboardingBeforeLoginUseCase,
    private val hasSeenOnboardingAfterLoginUseCase: CheckSeenOnboardingAfterLoginUseCase
) : BaseViewModel<Unit, SplashEvent>(Unit) {
    fun checkAndNavigate() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            delay(500)
            if (checkLoggedInUseCase()) {
                hasSeenOnboardingAfterLoginUseCase().onSuccess { hasSeen ->
                    if (hasSeen) {
                        emitEvent(SplashEvent.NavigateToHome)
                    } else {
                        emitEvent(SplashEvent.NavigateToOnboardingAfterLogin)
                    }
                }.onError {
                    emitError(it.message)
                }

            } else if (hasSeenOnboardingBeforeLoginUseCase()) {
                emitEvent(SplashEvent.NavigateToLogin)
            } else {
                emitEvent(SplashEvent.NavigateToOnboardingBeforeLogin)
            }
            setLoading(false)
        }
    }
}