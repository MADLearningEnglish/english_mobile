package com.mit.learning_english.presentation.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Base ViewModel theo pattern MVI (Model-View-Intent).
 * - Loading: StateFlow riêng (isLoading).
 * - Error: SharedFlow one-time event (errorEvent).
 * - Feature state: StateFlow generic (uiState).
 *
 * @param STATE Kiểu UI state chứa dữ liệu feature-specific
 * @param EVENT Kiểu one-time events (navigation, snackbar, dialog...)
 */
abstract class BaseViewModel<STATE : Any, EVENT>(
    initialState: STATE
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _event = MutableSharedFlow<EVENT>(extraBufferCapacity = 1)
    val event: SharedFlow<EVENT> = _event.asSharedFlow()

    private val _errorEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    protected fun setState(reducer: STATE.() -> STATE) {
        _uiState.update { it.reducer() }
    }

    protected fun emitEvent(event: EVENT) {
        _event.tryEmit(event)
    }

    protected fun emitError(message: String) {
        _errorEvent.tryEmit(message)
    }

    protected fun setLoading(loading: Boolean) {
        _isLoading.value = loading
    }

    /**
     * Xử lý khi có exception. Override để custom (log, analytics...).
     */
    protected open fun onError(throwable: Throwable) {
        _isLoading.value = false
        emitError(throwable.message ?: "Unknown error")
    }
}
