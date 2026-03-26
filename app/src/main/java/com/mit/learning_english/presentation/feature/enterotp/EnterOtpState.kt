package com.mit.learning_english.presentation.feature.enterotp

data class EnterOtpState(
    val otp: String? = null,
    val email: String? = null,
    val serverError: String? = null
)
