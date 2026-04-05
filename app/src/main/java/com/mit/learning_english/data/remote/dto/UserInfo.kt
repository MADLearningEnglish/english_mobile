package com.mit.learning_english.data.remote.dto

/**
 * Thông tin user (nếu API trả về)
 */
data class UserInfo(
    val email: String, val fullName: String, val avatar: String?, val role: Int
)