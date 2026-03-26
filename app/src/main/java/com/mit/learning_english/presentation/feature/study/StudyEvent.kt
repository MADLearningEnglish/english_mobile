package com.mit.learning_english.presentation.feature.study

sealed class StudyEvent {
    object NavigateBack : StudyEvent()
    object SessionComplete : StudyEvent()
}
