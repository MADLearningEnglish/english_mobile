package com.mit.learning_english.presentation.feature.onboardingafterlogin

sealed class OnboardingSecondEvent {
    data object AdvancePage : OnboardingSecondEvent()
    data object Complete : OnboardingSecondEvent()
}
