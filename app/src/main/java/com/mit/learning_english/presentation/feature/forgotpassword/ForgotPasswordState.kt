package com.mit.learning_english.presentation.feature.forgotpassword

data class ForgotPasswordState(
    val email: String? = null,
    val serverError: String? = null,
)
