package com.mit.learning_english.presentation.feature.forgotpassword

sealed class ResetPasswordEvent {
    object NavigateToLogin : ResetPasswordEvent()
}
