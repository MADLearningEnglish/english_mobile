package com.mit.learning_english.data.remote.retrofit

import com.mit.learning_english.data.local.datastore.PreferencesDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager để quản lý authentication token
 *
 * Token được lưu trữ an toàn trong DataStore với mã hóa bằng Android Keystore.
 * Plaintext token được cache trong memory để tránh decrypt lặp lại mỗi API call.
 * Cache tự động invalidate khi save/clear token.
 */
@Singleton
class AuthManager @Inject constructor(
    private val preferencesDatasource: PreferencesDatasource
) {
    @Volatile
    private var cachedAccessToken: String? = null

    @Volatile
    private var cachedRefreshToken: String? = null

    private val tokenMutex = Mutex()

    suspend fun saveToken(token: String) {
        preferencesDatasource.saveUserToken(token)
        cachedAccessToken = token
    }

    fun getToken(): Flow<String> {
        return preferencesDatasource.getUserToken()
    }

    /**
     * Trả về plaintext access token từ cache nếu có,
     * nếu chưa cache thì decrypt từ DataStore rồi cache lại.
     */
    suspend fun getTokenOnce(): String {
        cachedAccessToken?.let { return it }

        return tokenMutex.withLock {
            cachedAccessToken?.let { return@withLock it }
            val token = preferencesDatasource.getUserTokenOnce()
            cachedAccessToken = token.ifEmpty { null }
            token
        }
    }

    suspend fun hasToken(): Boolean {
        return getTokenOnce().isNotEmpty()
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        preferencesDatasource.saveRefreshToken(refreshToken)
        cachedRefreshToken = refreshToken
    }

    suspend fun getRefreshToken(): String {
        cachedRefreshToken?.let { return it }

        return tokenMutex.withLock {
            cachedRefreshToken?.let { return@withLock it }
            val token = preferencesDatasource.getRefreshTokenOnce()
            cachedRefreshToken = token.ifEmpty { null }
            token
        }
    }

    suspend fun clearToken() {
        cachedAccessToken = null
        cachedRefreshToken = null
        preferencesDatasource.removeKey(
            com.mit.learning_english.data.local.datastore.PreferencesKeys.USER_TOKEN
        )
        preferencesDatasource.removeKey(
            com.mit.learning_english.data.local.datastore.PreferencesKeys.REFRESH_TOKEN
        )
        preferencesDatasource.removeKey(
            com.mit.learning_english.data.local.datastore.PreferencesKeys.EXPIRES_AT
        )
    }

    suspend fun saveTokens(
        accessToken: String?, refreshToken: String? = null, expiresAt: Long? = null
    ) {
        accessToken?.let { saveToken(it) }
        refreshToken?.let { saveRefreshToken(it) }
        expiresAt?.let {
            preferencesDatasource.saveExpiresTime(it)
        }
    }

    suspend fun isValidLoggedIn(): Boolean {
        val expiresTime: Long = preferencesDatasource.getExpiresTime().first()
        return System.currentTimeMillis() < expiresTime - 60000L
    }
}