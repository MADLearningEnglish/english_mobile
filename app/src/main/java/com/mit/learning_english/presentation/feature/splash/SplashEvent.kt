package com.mit.learning_english.presentation.feature.splash

sealed class SplashEvent {
    object NavigateToLogin : SplashEvent()
    object NavigateToOnboardingBeforeLogin: SplashEvent()
    object NavigateToOnboardingAfterLogin: SplashEvent()
    object NavigateToHome : SplashEvent()
}