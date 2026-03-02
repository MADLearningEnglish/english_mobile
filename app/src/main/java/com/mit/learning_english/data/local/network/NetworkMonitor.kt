package com.mit.learning_english.data.local.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.mit.learning_english.domain.model.NetworkStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitor để theo dõi trạng thái kết nối mạng
 * 
 * Sử dụng ConnectivityManager để detect network changes và emit NetworkStatus
 */
@Singleton
class NetworkMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Kiểm tra trạng thái mạng hiện tại một lần
     * 
     * @return NetworkStatus.Online nếu có internet, NetworkStatus.Offline nếu không
     */
    fun getCurrentNetworkStatus(): NetworkStatus {
        val network = connectivityManager.activeNetwork ?: return NetworkStatus.Offline
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkStatus.Offline
        
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkStatus.Online
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkStatus.Online
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkStatus.Online
            else -> NetworkStatus.Offline
        }
    }

    /**
     * Flow để observe thay đổi trạng thái mạng
     * 
     * @return Flow<NetworkStatus> emit NetworkStatus khi có thay đổi
     */
    fun observeNetworkStatus(): Flow<NetworkStatus> = callbackFlow {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Online)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Offline)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(if (hasInternet) NetworkStatus.Online else NetworkStatus.Offline)
            }
        }

        connectivityManager.registerNetworkCallback(networkRequest, callback)

        // Emit giá trị ban đầu
        trySend(getCurrentNetworkStatus())

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }
}
