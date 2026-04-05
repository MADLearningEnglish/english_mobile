package com.mit.learning_english.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("fileName") val fileName: String,
    @SerializedName("originalName") val originalName: String,
    @SerializedName("contentType") val contentType: String,
    @SerializedName("size") val size: Long,
    @SerializedName("path") val path: String,
    @SerializedName("url") val url: String
)
