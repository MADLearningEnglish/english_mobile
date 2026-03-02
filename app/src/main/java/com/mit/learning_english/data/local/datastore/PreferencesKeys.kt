package com.mit.learning_english.data.local.datastore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Định nghĩa các keys cho DataStore Preferences
 *
 * Đây là nơi định nghĩa tất cả các keys sẽ được sử dụng để lưu trữ dữ liệu
 * trong DataStore. Mỗi key phải có kiểu dữ liệu cụ thể (String, Int, Boolean, etc.)
 */
object PreferencesKeys {
    // String keys
    val USER_TOKEN = stringPreferencesKey("user_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val LANGUAGE = stringPreferencesKey("language")

    // Int keys
    val USER_ID = intPreferencesKey("user_id")
    val THEME_MODE = intPreferencesKey("theme_mode") // 0 = Light, 1 = Dark, 2 = System

    // Boolean keys
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
}