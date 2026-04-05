package com.mit.learning_english.domain.model

data class TextLookupResult(
    val selectedText: String,
    val meaning: String,
    val phonetic: String?,
    val audioUrl: String?,
    val examples: List<String>
)
