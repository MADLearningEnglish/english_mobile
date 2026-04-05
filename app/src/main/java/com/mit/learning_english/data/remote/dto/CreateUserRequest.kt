package com.mit.learning_english.data.remote.dto

data class CreateUserRequest(
    val email: String,
    val password: String,
    val fullName: String
)