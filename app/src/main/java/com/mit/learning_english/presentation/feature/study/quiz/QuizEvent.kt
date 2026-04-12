package com.mit.learning_english.presentation.feature.study.quiz

sealed class QuizEvent {
    object NavigateBack : QuizEvent()
    object SessionComplete : QuizEvent()
}
