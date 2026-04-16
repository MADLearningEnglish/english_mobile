package com.mit.learning_english.shared

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteChangeNotifier @Inject constructor() {
    private val _favoriteChanged = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val favoriteChanged = _favoriteChanged.asSharedFlow()

    fun notifyChanged() {
        _favoriteChanged.tryEmit(Unit)
    }
}
