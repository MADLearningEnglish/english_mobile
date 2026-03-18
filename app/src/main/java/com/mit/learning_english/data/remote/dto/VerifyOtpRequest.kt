package com.mit.learning_english.data.remote.dto

data class VerifyOtpRequest(
    val email: String,
    val otp: String
)
