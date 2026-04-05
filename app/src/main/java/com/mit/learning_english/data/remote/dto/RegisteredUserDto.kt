package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Payload `data` khi POST /api/user/v1 (đăng ký) — server trả entity User (password bị @JsonIgnore).
 * Các field thừa từ BaseEntity (createdAt, …) Gson bỏ qua nếu không khai báo.
 */
data class RegisteredUserDto(
    val id: Int? = null,
    val email: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    val avatar: String? = null,
    val role: Int? = null,
    val status: Int? = null,
)
