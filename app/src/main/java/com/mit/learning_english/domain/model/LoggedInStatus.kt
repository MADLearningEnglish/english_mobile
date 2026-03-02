package com.mit.learning_english.domain.model

sealed class LoggedInStatus {
    object LoggedOut: LoggedInStatus()
    object LoggedIn: LoggedInStatus()
}
