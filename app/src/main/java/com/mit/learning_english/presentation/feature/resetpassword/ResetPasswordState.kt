package com.mit.learning_english.presentation.feature.resetpassword

data class ResetPasswordState(
    val password: String? = null,
    val rePassword: String? = null,
    val email: String? = null,
    val otp: String? = null,
    val serverError: String? = null
)
