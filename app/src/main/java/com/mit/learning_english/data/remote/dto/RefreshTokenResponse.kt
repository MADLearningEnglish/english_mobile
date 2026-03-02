package com.mit.learning_english.data.remote.dto

/**
 * Response từ refresh token endpoint
 */
data class RefreshTokenResponse(
    val accessToken: String,
    val refreshToken: String? = null, // Optional, có thể không trả về
    val expiresAt: Long,
)