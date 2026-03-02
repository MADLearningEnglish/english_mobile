package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.ThemeMode
import com.mit.learning_english.domain.repository.PreferencesRepository
import javax.inject.Inject

class SaveThemeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    suspend operator fun invoke(themeMode: ThemeMode){
        return preferencesRepository.saveTheme(themeMode)
    }
}