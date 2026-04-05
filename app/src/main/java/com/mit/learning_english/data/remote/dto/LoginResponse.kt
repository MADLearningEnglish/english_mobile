package com.mit.learning_english.data.remote.dto


/**
 * Response từ login endpoint
 */
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long? = null,
    val user: UserInfo? = null
)