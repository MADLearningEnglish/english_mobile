package com.mit.learning_english.presentation.feature.readbook

sealed class ReadBookEvent {
    object ShareBook : ReadBookEvent()
    object ShowTabBar : ReadBookEvent()
}