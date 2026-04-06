package com.mit.learning_english.presentation.feature.onboardingbeforelogin

sealed class OnboardingEvent {
    object NavigateToLogin: OnboardingEvent()
}