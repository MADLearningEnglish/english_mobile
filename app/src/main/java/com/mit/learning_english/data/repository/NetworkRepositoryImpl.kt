package com.mit.learning_english.data.repository

import com.mit.learning_english.data.local.network.NetworkMonitor
import com.mit.learning_english.domain.model.NetworkStatus
import com.mit.learning_english.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation của NetworkRepository
 * 
 * Data layer - wrap NetworkMonitor, che giấu chi tiết implementation
 */
class NetworkRepositoryImpl @Inject constructor(
    private val networkMonitor: NetworkMonitor
) : NetworkRepository {

    override fun getCurrentNetworkStatus(): NetworkStatus {
        return networkMonitor.getCurrentNetworkStatus()
    }

    override fun observeNetworkStatus(): Flow<NetworkStatus> {
        return networkMonitor.observeNetworkStatus()
    }
}
