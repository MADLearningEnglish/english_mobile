package com.mit.learning_english.data.repository

import com.mit.learning_english.data.local.datastore.PreferencesDatasource
import com.mit.learning_english.data.local.datastore.PreferencesKeys
import com.mit.learning_english.domain.model.ThemeMode
import com.mit.learning_english.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val preferencesDatasource: PreferencesDatasource
) : PreferencesRepository {
    override fun getTheme(): Flow<ThemeMode> {
        return preferencesDatasource.getInteger(PreferencesKeys.THEME_MODE, 2).map { value ->
            ThemeMode.fromCode(value)
        }
    }

    override suspend fun saveTheme(themeMode: ThemeMode) {
        preferencesDatasource.saveInteger(PreferencesKeys.THEME_MODE, themeMode.code)
    }

    override suspend fun clearAll() {
        preferencesDatasource.clearAll()
    }

}