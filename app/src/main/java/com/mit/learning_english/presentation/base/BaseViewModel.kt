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
 * Loading và error nằm trong STATE (BaseUiState) - single source of truth.
 *
 * @param STATE Kiểu UI state, phải implement BaseUiState
 * @param EVENT Kiểu one-time events (navigation, snackbar, dialog...)
 * @param initialState State khởi tạo ban đầu
 */
abstract class BaseViewModel<STATE : BaseUiState<STATE>, EVENT>(
    initialState: STATE
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<STATE> = _uiState.asStateFlow()
    private val _event = MutableSharedFlow<EVENT>(extraBufferCapacity = 1)
    val event: SharedFlow<EVENT> = _event.asSharedFlow()

    /**
     * CoroutineExceptionHandler dùng cho viewModelScope.launch.
     */
    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    /**
     * Cập nhật UI state bằng reducer.
     */
    protected fun setState(reducer: STATE.() -> STATE) {
        _uiState.update { it.reducer() }
    }

    /**
     * Emit one-time event (navigation, snackbar, dialog...).
     */
    protected fun emitEvent(event: EVENT) {
        _event.tryEmit(event)
    }

    /**
     * Cập nhật loading trong state.
     */
    protected fun setLoading(loading: Boolean) {
        setState { copyWith(isLoading = loading) }
    }

    /**
     * Cập nhật error message trong state.
     */
    protected fun setError(message: String?) {
        setState { copyWith(errorMessage = message) }
    }

    /**
     * Xóa error trong state.
     */
    protected fun clearError() {
        setState { copyWith(errorMessage = null) }
    }

    /**
     * Xử lý khi có exception. Override để custom (log, analytics...).
     */
    protected open fun onError(throwable: Throwable) {
        setState {
            copyWith(
                isLoading = false,
                errorMessage = throwable.message ?: "Unknown error"
            )
        }
    }
}
