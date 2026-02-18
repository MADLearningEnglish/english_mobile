package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.NetworkStatus
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface cho network connectivity
 * 
 * Domain layer - không phụ thuộc vào implementation cụ thể (NetworkMonitor, ConnectivityManager, etc.)
 */
interface NetworkRepository {
    
    /**
     * Lấy trạng thái mạng hiện tại một lần
     */
    fun getCurrentNetworkStatus(): NetworkStatus
    
    /**
     * Observe thay đổi trạng thái mạng
     */
    fun observeNetworkStatus(): Flow<NetworkStatus>
}
