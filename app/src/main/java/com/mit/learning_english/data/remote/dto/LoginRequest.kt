package com.mit.learning_english.data.remote.dto

/**
 * Request body cho login
 */
data class LoginRequest(
    val email: String, val password: String
)