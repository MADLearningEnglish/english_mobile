package com.mit.learning_english.presentation.feature.readbook

enum class ReadMode(val value: Int) {
    ReadMode(0), ListenMode(1);

    companion object {
        fun fromValue(value: Int): ReadMode {
            return entries.find { it.value == value } ?: ReadMode
        }
    }
}