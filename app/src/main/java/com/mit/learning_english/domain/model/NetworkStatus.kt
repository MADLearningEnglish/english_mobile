package com.mit.learning_english.domain.model

sealed class NetworkStatus {
    object Online: NetworkStatus()
    object Offline: NetworkStatus()
}