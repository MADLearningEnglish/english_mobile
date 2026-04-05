package com.mit.learning_english.presentation.feature.enterotp

sealed class EnterOtpEvent {
    object NavigateToResetPassword : EnterOtpEvent()
}