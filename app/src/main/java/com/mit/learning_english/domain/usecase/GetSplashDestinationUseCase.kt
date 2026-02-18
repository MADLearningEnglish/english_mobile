package com.mit.learning_english.domain.usecase

import com.mit.learning_english.domain.model.NetworkStatus
import com.mit.learning_english.domain.model.SplashDestination
import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.repository.NetworkRepository
import com.mit.learning_english.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case xử lý toàn bộ logic Splash screen
 * 
 * Orchestrate AuthRepository + NetworkRepository theo đúng business logic:
 * - No token → Login
 * - Has token + Online → checkLoggedIn → Home/Login
 * - Has token + Offline → HomeOffline, observe network → khi online lại checkLoggedIn
 * 
 * ViewModel chỉ inject UseCase này, không phụ thuộc Data layer
 */
class GetSplashDestinationUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val networkRepository: NetworkRepository
) {
    /**
     * Emit SplashDestination dựa trên logic business
     * 
     * Có thể emit nhiều lần khi: offline → navigate HomeOffline → online lại → emit HomeOnline/Login
     */
    operator fun invoke(): Flow<SplashDestination> = flow {
        // 1. Kiểm tra token
        if (!authRepository.hasToken()) {
            emit(SplashDestination.Login)
            return@flow
        }

        // 2. Có token → Kiểm tra network
        val networkStatus = networkRepository.getCurrentNetworkStatus()

        if (networkStatus is NetworkStatus.Online) {
            // Has internet → checkLoggedIn
            emit(handleCheckLoggedIn())
        } else {
            // No internet → Home (offline mode)
            emit(SplashDestination.HomeOffline)
            // Observe network để khi internet quay lại thì checkLoggedIn
            networkRepository.observeNetworkStatus().collect { status ->
                if (status is NetworkStatus.Online) {
                    emit(handleCheckLoggedIn())
                }
            }
        }
    }

    private suspend fun handleCheckLoggedIn(): SplashDestination {
        return when (val result = authRepository.checkLoggedIn()) {
            is Result.Success -> if (result.data) SplashDestination.HomeOnline else SplashDestination.Login
            is Result.Error -> SplashDestination.Login
            else -> SplashDestination.Login
        }
    }
}
