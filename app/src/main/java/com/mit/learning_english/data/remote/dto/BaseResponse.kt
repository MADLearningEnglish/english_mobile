package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Lớp wrapper chung cho tất cả các response từ API
 * Toàn bộ các API đều trả về format:
 * {
 *   "message": "...",
 *   "statusCode": 200,
 *   "data": { ... }
 * }
 */
data class BaseResponse<T>(
    @SerializedName("message")
    val message: String?,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("data")
    val data: T?
)
