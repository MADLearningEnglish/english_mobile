package com.mit.learning_english.data.remote.dto

/**
 * Request body cho login
 */
data class LoginRequest(
    val username: String,
    val password: String
)