package com.mit.learning_english.presentation.feature.forgotpassword

sealed class EnterOtpEvent {
    object NavigateToResetPassword : EnterOtpEvent()
}
