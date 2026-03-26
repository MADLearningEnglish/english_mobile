package com.mit.learning_english.presentation.feature.resetpassword

sealed class ResetPasswordEvent {
    object NavigateToLogin : ResetPasswordEvent()
}
