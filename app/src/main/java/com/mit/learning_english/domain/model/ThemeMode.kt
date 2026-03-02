package com.mit.learning_english.domain.model

enum class ThemeMode(val code: Int) {
    LIGHT(0),
    DARK(1),
    SYSTEM(2);

    companion object {
        fun fromCode(code: Int): ThemeMode {
            return entries.find { it.code == code } ?: SYSTEM
        }
    }
}