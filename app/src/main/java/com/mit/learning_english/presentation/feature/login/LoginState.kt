package com.mit.learning_english.presentation.feature.login

data class LoginState(
    val email: String? = null,
    val password: String? = null,
    val isSuccess: Boolean? = null,
)
