package com.mit.learning_english.data.remote.dto

data class DictionaryEntryDto(
    val word: String,
    val phonetics: List<PhoneticDto>?
)

data class PhoneticDto(
    val text: String?,
    val audio: String?
)
