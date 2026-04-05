package com.mit.learning_english.data.remote.retrofit.okhttpclient

import android.util.Log
import com.mit.learning_english.data.remote.api.AuthApiService
import com.mit.learning_english.data.remote.dto.RefreshTokenRequest
import com.mit.learning_english.data.remote.retrofit.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Authenticator để tự động refresh token khi nhận được 401 Unauthorized
 * 
 * Cách hoạt động:
 * 1. Khi API trả về 401 (Unauthorized), OkHttp sẽ gọi authenticate()
 * 2. Authenticator sẽ gọi API refresh token để lấy access token mới
 * 3. Nếu refresh thành công, retry request ban đầu với token mới
 * 4. Nếu refresh thất bại, trả về null để dừng retry
 * 
 * Lưu ý:
 * - Authenticator chỉ được gọi khi response code là 401 hoặc 407
 * - Cần thêm Authenticator vào OkHttpClient trong NetworkModule
 * - Sử dụng runBlocking vì OkHttp Authenticator không hỗ trợ suspend functions
 */
@Singleton
class TokenAuthenticator @Inject constructor(
    private val authManager: AuthManager,
    private val authApiService: AuthApiService
) : Authenticator {

    companion object {
        private const val TAG = "TokenAuthenticator"
        private const val MAX_RETRY_COUNT = 1 // Chỉ retry 1 lần để tránh infinite loop
    }

    /**
     * Được gọi khi nhận được 401 Unauthorized response
     * 
     * @param response Response 401 từ server
     * @param route Route của request
     * @return Request mới với token đã refresh, hoặc null để dừng retry
     */
    override fun authenticate(route: Route?, response: Response): Request? {
        // Kiểm tra số lần đã retry (tránh infinite loop)
        val retryCount = responseCount(response)
        if (retryCount >= MAX_RETRY_COUNT) {
            Log.w(TAG, "Max retry count reached, stopping retry")
            return null // Dừng retry
        }

        // Chỉ xử lý 401 Unauthorized
        if (response.code != 401) {
            Log.d(TAG, "Response code is not 401, skipping authentication")
            return null
        }

        return runBlocking {
            try {
                // Lấy refresh token
                val refreshToken = authManager.getRefreshToken()
                
                if (refreshToken.isEmpty()) {
                    Log.w(TAG, "No refresh token available, cannot refresh access token")
                    // Không có refresh token, có thể logout user
                    authManager.clearToken()
                    return@runBlocking null
                }

                // Gọi API refresh token
                Log.d(TAG, "Attempting to refresh access token")
                val refreshResponse = authApiService.refreshToken(
                    RefreshTokenRequest(refreshToken = refreshToken)
                )

                if (refreshResponse.isSuccessful) {
                    val baseResponse = refreshResponse.body()
                    val refreshTokenResponse = baseResponse?.data
                    
                    if (refreshTokenResponse != null) {
                        // Lưu access token mới
                        authManager.saveTokens(
                            accessToken = refreshTokenResponse.accessToken,
                            refreshToken = refreshTokenResponse.refreshToken,
                            expiresAt = refreshTokenResponse.expiresAt
                        )
                        
                        Log.d(TAG, "Token refreshed successfully")
                        
                        // Retry request ban đầu với token mới
                        return@runBlocking response.request.newBuilder()
                            .header("Authorization", "Bearer ${refreshTokenResponse.accessToken}")
                            .build()
                    } else {
                        Log.e(TAG, "Refresh token response data is null or error: ${baseResponse?.message}")
                    }
                } else {
                    Log.e(TAG, "Refresh token failed with code: ${refreshResponse.code()}")
                    
                    // Nếu refresh token cũng hết hạn, logout user
                    if (refreshResponse.code() == 401) {
                        Log.w(TAG, "Refresh token expired, clearing tokens")
                        authManager.clearToken()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing token", e)
            }
            
            // Nếu có lỗi, không retry
            null
        }
    }

    /**
     * Đếm số lần đã retry request này
     * 
     * OkHttp tự động thêm tag vào response để track retry count
     */
    private fun responseCount(response: Response): Int {
        var result = 1
        var currentResponse: Response? = response
        while (currentResponse != null) {
            currentResponse = currentResponse.priorResponse
            result++
        }
        return result
    }
}
