package com.mit.learning_english.presentation.feature.splash

import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.usecase.CheckLoggedInUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class định nghĩa navigation events từ Splash (Presentation layer)
 */
/**
 * SplashViewModel - Clean Architecture
 *
 * Chỉ phụ thuộc Domain layer (UseCase).
 * Không inject TokenManager, NetworkMonitor hay bất kỳ Data layer component nào.
 *
 * Flow: ViewModel → UseCase → Repository (interface) → RepositoryImpl (data)
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val checkLoggedInUseCase: CheckLoggedInUseCase
) : BaseViewModel<SplashState, SplashEvent>(SplashState()) {
    /**
     * Khởi động logic - chỉ gọi UseCase, map domain model sang UI event
     */
    fun checkAndNavigate() {
        viewModelScope.launch(exceptionHandler) {
            setLoading(true)
            delay(500)
            if (checkLoggedInUseCase()) {
                emitEvent(SplashEvent.NavigateToHome)
            } else (emitEvent(SplashEvent.NavigateToLogin))
            setState {
                copyWith(isLoading = true)
            }
            setLoading(false)
        }
    }
}
