package com.mit.learning_english.data.repository

import android.util.Log
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.AuthApiService
import com.mit.learning_english.data.remote.dto.CreateUserRequest
import com.mit.learning_english.data.remote.retrofit.AuthManager
import com.mit.learning_english.domain.model.LoginRequest
import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import javax.inject.Inject
import com.mit.learning_english.data.remote.dto.LoginRequest as LoginRequestDto

/**
 * Implementation của AuthRepository
 * 
 * Chịu trách nhiệm gọi API và xử lý authentication logic
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val userApiService: com.mit.learning_english.data.remote.api.UserApiService,
    private val authManager: AuthManager,
    private val resultMapper: ResultMapper,
) : AuthRepository {

    override suspend fun hasToken(): Boolean {
        return authManager.hasToken()
    }

    override suspend fun login(loginRequest: LoginRequest): Result<Boolean> {
        return try {
            val requestDto = LoginRequestDto(
                email = loginRequest.username,
                password = loginRequest.password
            )
            val response = authApiService.login(requestDto)
            Log.d("LoginResponse1", "${response.body()}")
            when (val result = resultMapper.fromResponse(response)) {
                is Result.Success -> {
                    val baseResponse = result.data
                    val loginResponse = baseResponse.data
                    
                    if (loginResponse != null) {
                        // Lưu token sau khi login thành công
                        Log.d("LoginResponse", "$loginResponse")
                        authManager.saveTokens(
                            accessToken = loginResponse.accessToken,
                            refreshToken = loginResponse.refreshToken,
                            expiresAt = loginResponse.expiresAt
                        )
                        Result.Success(true)
                    } else {
                        Result.Error(baseResponse.message ?: "Login failed")
                    }
                }
                is Result.Error -> result
                else -> Result.Error("Unknown error")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
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
            val response =  authApiService.checkLoggedIn()
            
            when (val result = resultMapper.fromResponse(response)) {
                is Result.Success -> {
                    val baseResponse = result.data
                    // API trả về 200 với UserInfo trong data -> Token hợp lệ
                    if (baseResponse.data != null) {
                        Result.Success(true)
                    } else {
                        Result.Success(false)
                    }
                }
                is Result.Error -> {
                    // Xử lý các trường hợp lỗi
                    when (result.code) {
                        401, 403 -> {
                            // Token không hợp lệ hoặc hết hạn
                            // Xóa token khỏi local storage để tránh gọi lại với token cũ
                            authManager.clearToken()

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
            resultMapper.fromException(e)
        }
    }

    override suspend fun isValidLoggedIn(): Boolean {
        return authManager.isValidLoggedIn()
    }

    override suspend fun signUp(email: String, password: String, fullName: String): com.mit.learning_english.domain.util.Result<Boolean> {
        return try {
            val request = CreateUserRequest(
                email = email,
                password = password,
                fullName = fullName
            )
            val response = userApiService.createUser(request)
            when (val result = resultMapper.fromResponse(response)) {
                is com.mit.learning_english.domain.util.Result.Success -> {
                    // If API returns success (message/data), treat as success
                    com.mit.learning_english.domain.util.Result.Success(true)
                }
                is com.mit.learning_english.domain.util.Result.Error -> result
                else -> com.mit.learning_english.domain.util.Result.Error("Unknown error")
    override suspend fun requestForgotPasswordOtp(email: String): Result<Boolean> {
        return try {
            val request = com.mit.learning_english.data.remote.dto.ForgotPasswordRequest(email = email)
            val response = authApiService.requestForgotPasswordOtp(request)
            when (val result = resultMapper.fromResponse(response)) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
                else -> Result.Error("Unknown error")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun verifyForgotPasswordOtp(email: String, otp: String): Result<Boolean> {
        return try {
            val request = com.mit.learning_english.data.remote.dto.VerifyOtpRequest(email = email, otp = otp)
            val response = authApiService.verifyForgotPasswordOtp(request)
            when (val result = resultMapper.fromResponse(response)) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
                else -> Result.Error("Unknown error")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }

    override suspend fun resetForgotPassword(email: String, otp: String, newPassword: String): Result<Boolean> {
        return try {
            val request = com.mit.learning_english.data.remote.dto.ResetPasswordRequest(
                email = email,
                otp = otp,
                newPassword = newPassword
            )
            val response = authApiService.resetPassword(request)
            when (val result = resultMapper.fromResponse(response)) {
                is Result.Success -> Result.Success(true)
                is Result.Error -> result
                else -> Result.Error("Unknown error")
            }
        } catch (e: Exception) {
            resultMapper.fromException(e)
        }
    }


}
