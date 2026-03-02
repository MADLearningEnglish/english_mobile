package com.mit.learning_english.data.remote.retrofit

import com.mit.learning_english.data.local.datastore.PreferencesDatasource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager để quản lý authentication token
 * 
 * Token được lưu trữ an toàn trong DataStore với mã hóa bằng Android Keystore
 * 
 * Cách sử dụng:
 * 1. Inject TokenManager vào class cần dùng
 * 2. Gọi saveToken() sau khi login thành công
 * 3. Gọi getToken() để lấy token cho API calls
 * 4. Gọi clearToken() khi logout
 */
@Singleton
class TokenManager @Inject constructor(
    private val preferencesDatasource: PreferencesDatasource
) {
    
    /**
     * Lưu token (sẽ được mã hóa tự động)
     * 
     * @param token Token cần lưu
     */
    suspend fun saveToken(token: String) {
        preferencesDatasource.saveUserToken(token)
    }
    
    /**
     * Lấy token dạng Flow (để observe)
     * Token sẽ được giải mã tự động
     * 
     * @return Flow<String> chứa token đã giải mã
     */
    fun getToken(): Flow<String> {
        return preferencesDatasource.getUserToken()
    }
    
    /**
     * Lấy token một lần (không phải Flow)
     * Token sẽ được giải mã tự động
     * 
     * @return Token đã giải mã, hoặc chuỗi rỗng nếu không có
     */
    suspend fun getTokenOnce(): String {
        return preferencesDatasource.getUserTokenOnce()
    }
    
    /**
     * Kiểm tra xem đã có token chưa
     * 
     * @return true nếu có token, false nếu không
     */
    suspend fun hasToken(): Boolean {
        val token = getTokenOnce()
        return token.isNotEmpty()
    }
    
    /**
     * Lưu refresh token
     */
    suspend fun saveRefreshToken(refreshToken: String) {
        preferencesDatasource.saveRefreshToken(refreshToken)
    }
    
    /**
     * Lấy refresh token
     */
    suspend fun getRefreshToken(): String {
        return preferencesDatasource.getRefreshTokenOnce()
    }
    
    /**
     * Xóa token (logout)
     */
    suspend fun clearToken() {
        preferencesDatasource.removeKey(
            com.mit.learning_english.data.local.datastore.PreferencesKeys.USER_TOKEN
        )
        preferencesDatasource.removeKey(
            com.mit.learning_english.data.local.datastore.PreferencesKeys.REFRESH_TOKEN
        )
    }
    
    /**
     * Lưu cả access token và refresh token (sau khi login hoặc refresh)
     */
    suspend fun saveTokens(accessToken: String, refreshToken: String? = null) {
        saveToken(accessToken)
        refreshToken?.let { saveRefreshToken(it) }
    }
}