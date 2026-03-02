package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getTheme(): Flow<ThemeMode>
    suspend fun saveTheme(themeMode: ThemeMode)
    suspend fun clearAll()
}