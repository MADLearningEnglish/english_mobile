package com.mit.learning_english.data.remote.dto

data class TextLookupResponseDto(
    val selectedText: String,
    val meaning: String,
    val phonetic: String?,
    val audioUrl: String?,
    val examples: List<String>?
)
