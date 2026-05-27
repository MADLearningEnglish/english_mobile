package com.mit.learning_english.presentation.feature.readbook

/**
 * Trạng thái tra từ trong luồng đọc sách.
 */
enum class LookupStatus {
    Idle,
    Loading,
    Success,
    Error
}