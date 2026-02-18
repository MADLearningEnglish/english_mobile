package com.mit.learning_english.data.repository

import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.AuthApiService
import com.mit.learning_english.data.remote.dto.LoginRequest as LoginRequestDto
import com.mit.learning_english.data.remote.retrofit.TokenManager
import com.mit.learning_english.domain.model.LoginRequest
import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject

/**
 * Implementation của AuthRepository
 * 
 * Chịu trách nhiệm gọi API và xử lý authentication logic
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun hasToken(): Boolean {
        return tokenManager.hasToken()
    }

    override suspend fun login(loginRequest: LoginRequest): Result<Boolean> {
        return try {
            val requestDto = LoginRequestDto(
                username = loginRequest.username,
                password = loginRequest.password
            )
            val response = authApiService.login(requestDto)
            
            when (val result = ResultMapper.fromResponse(response)) {
                is Result.Success -> {
                    val loginResponse = result.data
                    // Lưu token sau khi login thành công
                    tokenManager.saveTokens(
                        accessToken = loginResponse.accessToken,
                        refreshToken = loginResponse.refreshToken
                    )
                    Result.Success(true)
                }
                is Result.Error -> result
                else -> Result.Error("Unknown error")
            }
        } catch (e: Exception) {
            ResultMapper.fromException(e)
        }
    }

    /**
     * Kiểm tra trạng thái đăng nhập hiện tại
     * 
     * Logic flow:
     * 1. Gọi API GET "auth/me" với token tự động được thêm vào header qua AuthInterceptor
     *    - Header: "Authorization: Bearer {access_token}"
     * 
     * 2. Server xử lý:
     *    - Kiểm tra token trong header Authorization
     *    - Validate token (kiểm tra signature, expiration, etc.)
     *    - Nếu hợp lệ → trả về 200 với UserInfo
     *    - Nếu không hợp lệ → trả về 401/403
     * 
     * 3. Client xử lý response:
     *    - Success (200): Token hợp lệ → return Result.Success(true)
     *    - Error 401/403: Token không hợp lệ hoặc hết hạn → 
     *      + Xóa token khỏi local storage
     *      + return Result.Success(false) để báo chưa đăng nhập
     *    - Error khác: Lỗi network/server → return Result.Error
     * 
     * @return Result<Boolean> 
     *         - Success(true): Đã đăng nhập và token hợp lệ
     *         - Success(false): Chưa đăng nhập hoặc token không hợp lệ (đã xóa token)
     *         - Error: Lỗi network hoặc server
     */
    override suspend fun checkLoggedIn(): Result<Boolean> {
        return try {
            // Gọi API - token sẽ tự động được thêm vào header qua AuthInterceptor
            val response = authApiService.checkLoggedIn()
            
            when (val result = ResultMapper.fromResponse(response)) {
                is Result.Success -> {
                    // API trả về 200 với UserInfo → Token hợp lệ, user đã đăng nhập
                    Result.Success(true)
                }
                is Result.Error -> {
                    // Xử lý các trường hợp lỗi
                    when (result.code) {
                        401, 403 -> {
                            // Token không hợp lệ hoặc hết hạn
                            // Xóa token khỏi local storage để tránh gọi lại với token cũ
                            tokenManager.clearToken()
                            // Trả về Success(false) để báo chưa đăng nhập (không phải lỗi)
                            Result.Success(false)
                        }
                        else -> {
                            // Lỗi khác (network error, server error, etc.)
                            result
                        }
                    }
                }
                else -> Result.Error("Unknown error")
            }
        } catch (e: Exception) {
            // Xử lý exception (network timeout, connection error, etc.)
            ResultMapper.fromException(e)
        }
    }
}
