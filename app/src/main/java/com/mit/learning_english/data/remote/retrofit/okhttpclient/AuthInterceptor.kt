package com.mit.learning_english.data.remote.retrofit.okhttpclient

import com.mit.learning_english.data.remote.retrofit.AuthManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Interceptor để tự động thêm Authorization header vào mọi request
 * 
 * Cách hoạt động:
 * 1. Intercept mọi request trước khi gửi đi
 * 2. Lấy access token từ TokenManager
 * 3. Thêm header "Authorization: Bearer {token}" vào request
 * 4. Nếu không có token, request sẽ được gửi đi không có Authorization header
 * 
 * Lưu ý:
 * - Interceptor này chỉ thêm token, không xử lý 401 response
 * - Authenticator sẽ xử lý 401 và refresh token
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val authManager: AuthManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Kiểm tra xem request đã có Authorization header chưa
        // Nếu có rồi thì không thêm nữa (tránh override)
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Lấy token (sử dụng runBlocking vì Interceptor không hỗ trợ suspend)
        val token = runBlocking {
            authManager.getTokenOnce()
        }

        // Nếu có token, thêm vào header
        val newRequest = if (token.isNotEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
