package com.mit.learning_english.presentation.feature.splash

sealed class SplashEvent {
    object NavigateToLogin : SplashEvent()
    object NavigateToHome : SplashEvent()
}