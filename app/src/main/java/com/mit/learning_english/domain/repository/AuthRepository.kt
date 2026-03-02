package com.mit.learning_english.domain.repository

import com.mit.learning_english.domain.model.LoginRequest
import com.mit.learning_english.domain.util.Result

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<Boolean>
    
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

}