package com.mit.learning_english.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mit.learning_english.domain.model.SplashDestination
import com.mit.learning_english.domain.usecase.GetSplashDestinationUseCase
import com.mit.learning_english.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Sealed class định nghĩa navigation events từ Splash (Presentation layer)
 */
sealed class SplashNavigationEvent {
    object NavigateToLogin : SplashNavigationEvent()
    object NavigateToHomeOnline : SplashNavigationEvent()
    object NavigateToHomeOffline : SplashNavigationEvent()
}

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
    private val getSplashDestinationUseCase: GetSplashDestinationUseCase
) : BaseViewModel() {

    private val _navigationEvent = MutableLiveData<SplashNavigationEvent>()
    val navigationEvent: LiveData<SplashNavigationEvent> = _navigationEvent

    /**
     * Khởi động logic - chỉ gọi UseCase, map domain model sang UI event
     */
    fun checkAndNavigate() {
        viewModelScope.launch(exceptionHandler) {
            getSplashDestinationUseCase()
                .catch { e ->
                    onError(e)
                    _navigationEvent.value = SplashNavigationEvent.NavigateToLogin
                }
                .collect { destination ->
                    _navigationEvent.value = destination.toNavigationEvent()
                }
        }
    }

    private fun SplashDestination.toNavigationEvent(): SplashNavigationEvent = when (this) {
        is SplashDestination.Login -> SplashNavigationEvent.NavigateToLogin
        is SplashDestination.HomeOnline -> SplashNavigationEvent.NavigateToHomeOnline
        is SplashDestination.HomeOffline -> SplashNavigationEvent.NavigateToHomeOffline
    }
}
