package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.LoginRequest
import com.mit.learning_english.domain.util.Result

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<Boolean>
    suspend fun signUp(email: String, password: String, fullName: String): Result<Boolean>

    /**
     * Kiểm tra có token trong local storage hay không
     *
     * @return true nếu có token, false nếu không
     */
    suspend fun hasToken(): Boolean

    /**
     * Kiểm tra trạng thái đăng nhập hiện tại
     *
     * @return Result<Boolean> true nếu đã đăng nhập và token hợp lệ, false nếu chưa
     */
    suspend fun checkLoggedIn(): Result<Boolean>

    suspend fun isValidLoggedIn(): Boolean

    // Forgotten password flow
    suspend fun requestForgotPasswordOtp(email: String): Result<Boolean>

    suspend fun verifyForgotPasswordOtp(email: String, otp: String): Result<Boolean>

    suspend fun resetForgotPassword(
        email: String,
        otp: String,
        newPassword: String
    ): Result<Boolean>
}