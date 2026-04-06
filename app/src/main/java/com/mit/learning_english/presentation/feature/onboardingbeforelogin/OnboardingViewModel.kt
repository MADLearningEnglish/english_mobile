package com.mit.learning_english.presentation.feature.onboardingbeforelogin

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.onboarding.UpdateOnboardingStatusUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val updateOnboardingStatusUseCase: UpdateOnboardingStatusUseCase
): BaseViewModel<OnboardingState,OnboardingEvent>(OnboardingState()) {

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            updateOnboardingStatusUseCase(true)
            emitEvent(OnboardingEvent.NavigateToLogin)
        }
    }
}