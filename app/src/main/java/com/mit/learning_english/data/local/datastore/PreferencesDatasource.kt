package com.mit.learning_english.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mit.learning_english.data.security.EncryptionService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository để quản lý DataStore Preferences
 *
 * Đây là ví dụ về cách sử dụng DataStore được inject từ Hilt.
 *
 * Cách hoạt động:
 * 1. DataStore được inject vào constructor thông qua @Inject
 * 2. Hilt tự động tìm provider trong DatabaseModule và inject instance vào đây
 * 3. EncryptionService được inject để mã hóa/giải mã token
 * 4. Repository cung cấp các hàm để đọc/ghi preferences
 */
@Singleton
class PreferencesDatasource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val encryptionService: EncryptionService
) {

    /**
     * Đọc giá trị String từ DataStore
     *
     * @param key Key của preference cần đọc
     * @param defaultValue Giá trị mặc định nếu key chưa tồn tại
     * @return Flow<String> - Flow để observe giá trị (tự động update khi giá trị thay đổi)
     */
    fun getString(key: Preferences.Key<String>, defaultValue: String = ""): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }

    /**
     * Ghi giá trị String vào DataStore
     *
     * @param key Key của preference
     * @param value Giá trị cần lưu
     */
    suspend fun saveString(key: Preferences.Key<String>, value: String) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    suspend fun saveInteger(key: Preferences.Key<Int>, value: Int){
        dataStore.edit { preferences ->
            preferences[key]=value
        }
    }
    suspend fun saveLong(key: Preferences.Key<Long>, value: Long){
        dataStore.edit { preferences ->
            preferences[key]=value
        }
    }

    fun getInteger(key: Preferences.Key<Int>, defaultValue: Int):Flow<Int>{
        return dataStore.data.map{ preferences ->
            preferences[key]?: defaultValue
        }
    }



    suspend fun saveExpiresTime(expiresAt: Long) {
        // Mã hóa token trước khi lưu
        val expiresAtString: String = expiresAt.toString()
        val encryptedToken = encryptionService.encrypt(expiresAtString)
        if (encryptedToken != null) {
            saveString(PreferencesKeys.EXPIRES_AT, encryptedToken)
        } else {
            // Nếu mã hóa thất bại, có thể log error hoặc throw exception
            // Ở đây chúng ta sẽ lưu plaintext như fallback (không khuyến khích trong production)
            // Trong production nên throw exception hoặc retry
            throw SecurityException("Failed to encrypt token")
        }
    }

    /**
     * Lưu user token đã được mã hóa
     * 
     * Token sẽ được mã hóa bằng EncryptionService trước khi lưu vào DataStore
     * để đảm bảo bảo mật ngay cả khi device bị root hoặc bị truy cập trái phép
     */
    suspend fun saveUserToken(token: String) {
        // Mã hóa token trước khi lưu
        val encryptedToken = encryptionService.encrypt(token)
        if (encryptedToken != null) {
            saveString(PreferencesKeys.USER_TOKEN, encryptedToken)
        } else {
            // Nếu mã hóa thất bại, có thể log error hoặc throw exception
            // Ở đây chúng ta sẽ lưu plaintext như fallback (không khuyến khích trong production)
            // Trong production nên throw exception hoặc retry
            throw SecurityException("Failed to encrypt token")
        }
    }

    /**
     * Đọc và giải mã user token
     * 
     * Token được đọc từ DataStore và giải mã về plaintext gốc
     * 
     * @return Flow<String> - Flow chứa token đã giải mã, hoặc chuỗi rỗng nếu không có hoặc giải mã thất bại
     */
    fun getUserToken(): Flow<String> {
        return getString(PreferencesKeys.USER_TOKEN).map { encryptedToken ->
            if (encryptedToken.isNotEmpty()) {
                // Giải mã token
                encryptionService.decrypt(encryptedToken) ?: ""
            } else {
                ""
            }
        }
    }
     fun getExpiresTime(): Flow<Long> {
        return getString(PreferencesKeys.EXPIRES_AT).map { encryptedExpiresTime ->
            if (encryptedExpiresTime.isNotEmpty()) {
                encryptionService.decrypt(encryptedExpiresTime)?.toLong()?: 0L
            } else {
                0L
            }
        }
    }
    
    /**
     * Lấy token một lần (không phải Flow)
     * 
     * Hữu ích khi cần token ngay lập tức mà không cần observe
     * 
     * @return Token đã giải mã, hoặc chuỗi rỗng nếu không có hoặc giải mã thất bại
     */
    suspend fun getUserTokenOnce(): String {
        val encryptedToken = getString(PreferencesKeys.USER_TOKEN).first()
        return if (encryptedToken.isNotEmpty()) {
            encryptionService.decrypt(encryptedToken) ?: ""
        } else {
            ""
        }
    }

    /**
     * Lưu refresh token đã được mã hóa
     */
    suspend fun saveRefreshToken(refreshToken: String) {
        val encryptedToken = encryptionService.encrypt(refreshToken)
        if (encryptedToken != null) {
            saveString(PreferencesKeys.REFRESH_TOKEN, encryptedToken)
        } else {
            throw SecurityException("Failed to encrypt refresh token")
        }
    }

    /**
     * Lấy refresh token đã giải mã
     */
    suspend fun getRefreshTokenOnce(): String {
        val encryptedToken = getString(PreferencesKeys.REFRESH_TOKEN).first()
        return if (encryptedToken.isNotEmpty()) {
            encryptionService.decrypt(encryptedToken) ?: ""
        } else {
            ""
        }
    }

    /**
     * Xóa một key
     */
    suspend fun removeKey(key: Preferences.Key<*>) {
        dataStore.edit { preferences ->
            preferences.remove(key)
        }
    }

    /**
     * Ví dụ: Xóa tất cả preferences
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun hasSeenBeforeLoginOnboarding():Boolean{
       return dataStore.data.map{ preferences ->
           preferences[PreferencesKeys.HAS_SEEN_BEFORE_LOGIN_ONBOARDING]?:false
       }.first()
    }

    suspend fun updateOnboardingStatus(hasSeen:Boolean){
         dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_SEEN_BEFORE_LOGIN_ONBOARDING] = hasSeen
        }
    }

    suspend fun hasCompletedAfterLoginOnboarding(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_AFTER_LOGIN_ONBOARDING] ?: false
        }.first()
    }

}