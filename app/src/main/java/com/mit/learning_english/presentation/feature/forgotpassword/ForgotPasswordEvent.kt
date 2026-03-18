package com.mit.learning_english.presentation.feature.forgotpassword

sealed class ForgotPasswordEvent {
    object NavigateToEnterOtp : ForgotPasswordEvent()
}
