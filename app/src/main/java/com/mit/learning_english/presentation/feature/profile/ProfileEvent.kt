package com.mit.learning_english.presentation.feature.profile

import java.time.LocalDate

sealed class ProfileEvent {
    data object OpenEditProfile : ProfileEvent()
    data object OpenVocabularyList : ProfileEvent()
    data object OpenCompletedExercises : ProfileEvent()
    data object OpenMyCorrections : ProfileEvent()
    data object OpenDailyActivity : ProfileEvent()
    data class OpenActivityDay(val date: LocalDate) : ProfileEvent()
}
