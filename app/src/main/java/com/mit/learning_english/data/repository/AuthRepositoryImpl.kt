package com.mit.learning_english.data.repository

import android.util.Log
import com.mit.learning_english.data.mapper.ResultMapper
import com.mit.learning_english.data.remote.api.AuthApiService
import com.mit.learning_english.data.remote.dto.CreateUserRequest
import com.mit.learning_english.data.remote.retrofit.AuthManager
import com.mit.learning_english.domain.model.LoginRequest
import com.mit.learning_english.domain.repository.AuthRepository
import com.mit.learning_english.domain.util.Result
import com.mit.learning_english.shared.UiErrorKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.mit.learning_english.data.remote.dto.LoginRequest as LoginRequestDto

/**
 * Implementation của AuthRepository
 * 
 * Chịu trách nhiệm gọi API và xử lý authentication logic
 */
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val authManager: AuthManager,
    private val resultMapper: ResultMapper,
) : AuthRepository {

    override suspend fun hasToken(): Boolean {
        return withContext(Dispatchers.IO) {
            authManager.hasToken()
        }
    }

    override suspend fun login(loginRequest: LoginRequest): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
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
                            Result.Error(baseResponse.message ?: UiErrorKey.LOGIN_FAILED)
                        }
                    }
                    is Result.Error -> result
                    else -> Result.Error(UiErrorKey.UNKNOWN)
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
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
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.checkLoggedIn()

                when (val result = resultMapper.fromResponse(response)) {
                    is Result.Success -> {
                        val baseResponse = result.data
                        if (baseResponse.data != null) {
                            Result.Success(true)
                        } else {
                            Result.Success(false)
                        }
                    }
                    is Result.Error -> {
                        when (result.code) {
                            401, 403 -> {
                                authManager.clearToken()
                                Result.Success(false)
                            }
                            else -> {
                                result
                            }
                        }
                    }
                    else -> Result.Error(UiErrorKey.UNKNOWN)
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun isValidLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            authManager.isValidLoggedIn()
        }
    }

    override suspend fun signUp(email: String, password: String, fullName: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = CreateUserRequest(
                    email = email,
                    password = password,
                    fullName = fullName
                )
                val response = authApiService.createUser(request)
                when (val result = resultMapper.fromResponse(response)) {
                    is Result.Success -> {
                        val base = result.data
                        if (base.data != null) {
                            Result.Success(true)
                        } else {
                            Result.Error(base.message ?: UiErrorKey.AUTH_SIGNUP_VI)
                        }
                    }
                    is Result.Error -> result
                    else -> Result.Error(UiErrorKey.UNKNOWN)
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }
    override suspend fun requestForgotPasswordOtp(email: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = com.mit.learning_english.data.remote.dto.ForgotPasswordRequest(email = email)
                val response = authApiService.requestForgotPasswordOtp(request)
                when (val result = resultMapper.fromResponse(response)) {
                    is Result.Success -> Result.Success(true)
                    is Result.Error -> result
                    else -> Result.Error(UiErrorKey.UNKNOWN)
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun verifyForgotPasswordOtp(email: String, otp: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = com.mit.learning_english.data.remote.dto.VerifyOtpRequest(email = email, otp = otp)
                val response = authApiService.verifyForgotPasswordOtp(request)
                when (val result = resultMapper.fromResponse(response)) {
                    is Result.Success -> Result.Success(true)
                    is Result.Error -> result
                    else -> Result.Error(UiErrorKey.UNKNOWN)
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }

    override suspend fun resetForgotPassword(email: String, otp: String, newPassword: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val request = com.mit.learning_english.data.remote.dto.ResetPasswordRequest(
                    email = email,
                    otp = otp,
                    newPassword = newPassword
                )
                val response = authApiService.resetPassword(request)
                when (val result = resultMapper.fromResponse(response)) {
                    is Result.Success -> Result.Success(true)
                    is Result.Error -> result
                    else -> Result.Error(UiErrorKey.UNKNOWN)
                }
            } catch (e: Exception) {
                resultMapper.fromException(e)
            }
        }
    }
}
