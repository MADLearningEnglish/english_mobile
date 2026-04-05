package com.mit.learning_english.presentation.feature.readbook

sealed class ReadBookEvent {
    object ShareBook : ReadBookEvent()
    data class GoToChapter(val index: Int) : ReadBookEvent()
    data class PlayAudio(val url: String) : ReadBookEvent()
    object StopAudio : ReadBookEvent()
}