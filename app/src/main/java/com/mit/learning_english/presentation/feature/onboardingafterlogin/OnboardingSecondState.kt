package com.mit.learning_english.presentation.feature.onboardingafterlogin

import com.mit.learning_english.domain.model.Genre
import com.mit.learning_english.domain.model.LearningLevel

data class OnboardingSecondState(
    val levels: List<LearningLevel> = emptyList(),
    val selectedLevelId: Int? = null,
    val genres: List<Genre> = emptyList(),
    val selectedGenreIds: Set<Int> = emptySet(),
    val levelsLoadFailed: Boolean = false
)
