package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.ThemeMode
import com.mit.learning_english.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) {
    operator fun invoke(): Flow<ThemeMode>{
        return preferencesRepository.getTheme()
    }
}