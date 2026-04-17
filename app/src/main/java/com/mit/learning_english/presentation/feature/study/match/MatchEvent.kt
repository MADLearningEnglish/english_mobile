package com.mit.learning_english.presentation.feature.study.match

sealed class MatchEvent {
    object NavigateBack : MatchEvent()
    object SessionComplete : MatchEvent()
}
