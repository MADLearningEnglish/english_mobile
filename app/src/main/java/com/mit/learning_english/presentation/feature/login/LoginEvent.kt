package com.mit.learning_english.presentation.feature.login

sealed class LoginEvent {
    object NavigateToHome : LoginEvent()
    object NavigateToOnboarding: LoginEvent()
    object NavigateToSignUp : LoginEvent()
}