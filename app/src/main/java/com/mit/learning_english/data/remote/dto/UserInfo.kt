package com.mit.learning_english.data.remote.dto

/**
 * Thông tin user (nếu API trả về)
 */
data class UserInfo(
    val id: String,
    val username: String,
    val email: String?
)