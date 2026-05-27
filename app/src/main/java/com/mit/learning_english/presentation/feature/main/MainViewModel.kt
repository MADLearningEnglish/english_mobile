package com.mit.learning_english.presentation.feature.main

import com.mit.learning_english.domain.usecase.auth.CheckLoggedInUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import com.mit.learning_english.presentation.feature.root.PendingDeepLinkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val pendingDeepLinkManager: PendingDeepLinkManager,
    private val checkLoggedInUseCase: CheckLoggedInUseCase
) : BaseViewModel<Unit, MainEvent>(Unit) {

    /** Expose cho Fragment observe. */
    val pendingDeepLinkBookId: StateFlow<Int?> = pendingDeepLinkManager.pendingBookId

    /**
     * Gate auth rồi mới consume. Trả về bookId để điều hướng, hoặc null nếu không có / chưa auth.
     */
    suspend fun tryConsumeDeepLink(): Int? {
        val pending = pendingDeepLinkManager.pendingBookId.value
        if (pending == null || pending <= 0) return null
        if (!checkLoggedInUseCase()) return null
        return pendingDeepLinkManager.consume()
    }
}
