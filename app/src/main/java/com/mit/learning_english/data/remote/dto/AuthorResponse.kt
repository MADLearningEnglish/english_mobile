package com.mit.learning_english.data.remote.dto

data class AuthorResponse(
    val id: Int,
    val name: String,
    val avatar: String,
    val nationality: String,
    val biography: String
)
