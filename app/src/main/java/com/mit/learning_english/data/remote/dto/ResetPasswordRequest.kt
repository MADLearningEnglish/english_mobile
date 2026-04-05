package com.mit.learning_english.data.remote.dto

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)
