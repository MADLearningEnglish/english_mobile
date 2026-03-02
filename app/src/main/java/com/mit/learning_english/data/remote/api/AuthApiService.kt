package com.mit.learning_english.data.remote.api

import com.mit.learning_english.data.remote.dto.LoginRequest
import com.mit.learning_english.data.remote.dto.LoginResponse
import com.mit.learning_english.data.remote.dto.RefreshTokenRequest
import com.mit.learning_english.data.remote.dto.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * API Service cho authentication
 * 
 * Định nghĩa các endpoint liên quan đến authentication như login, refresh token, etc.
 */
interface AuthApiService {
    
    /**
     * Refresh access token bằng refresh token
     * 
     * @param request Body chứa refresh token
     * @return Response chứa access token mới
     */
    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>
    
    /**
     * Login endpoint
     * 
     * @param request Body chứa username và password
     * @return Response chứa access token và refresh token
     */
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    
    /**
     * Kiểm tra trạng thái đăng nhập hiện tại
     * 
     * API này yêu cầu authentication token trong header:
     * - Header: "Authorization: Bearer {access_token}"
     * - Token được tự động thêm vào header qua AuthInterceptor
     * 
     * Server response:
     * - 200 OK: Token hợp lệ → trả về UserInfo
     * - 401 Unauthorized: Token không hợp lệ hoặc hết hạn
     * - 403 Forbidden: Token hợp lệ nhưng không có quyền truy cập
     * 
     * @return Response<UserInfo> chứa thông tin user nếu đã đăng nhập và token hợp lệ
     */
    @retrofit2.http.GET("auth/me")
    suspend fun checkLoggedIn(): Response<com.mit.learning_english.data.remote.dto.UserInfo>
}










