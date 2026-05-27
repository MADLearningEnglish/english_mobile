package com.mit.learning_english.presentation.feature.readbook

/**
 * Chế độ sử dụng của màn hình đọc sách: đọc văn bản hoặc nghe audio.
 */
enum class ReadMode(val value: Int) {
    ReadMode(0), ListenMode(1);

    companion object {
        /**
         * Quy đổi giá trị số sang enum chế độ đọc.
         */
        fun fromValue(value: Int): ReadMode {
            return entries.find { it.value == value } ?: ReadMode
        }
    }
}